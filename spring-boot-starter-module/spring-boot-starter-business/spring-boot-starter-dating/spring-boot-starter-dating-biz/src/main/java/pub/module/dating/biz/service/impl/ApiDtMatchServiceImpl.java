package pub.module.dating.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.dating.api.constants.CusMemberBenefitTypeEnum;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.MemberBenefitConsumeResultDTO;
import pub.module.dating.biz.util.DatingMemberBenefitConsumeUtil;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.constants.DatingErrorCodeEnum;
import pub.module.dating.api.constants.MatchRelationProgressCodeEnum;
import pub.module.dating.api.service.ApiDtCusMatchmakerRelService;
import pub.module.dating.api.service.ApiDtMatchService;
import pub.module.dating.api.service.dto.DtMatchDTO;
import pub.module.dating.api.service.dto.DtMatchUpdateRelationProgressVO;
import pub.module.dating.crud.entity.DtContact;
import pub.module.dating.crud.entity.DtMatch;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.service.DtContactService;
import pub.module.dating.biz.messaging.DatingWxSubscribeNotifyPublisher;
import pub.module.dating.crud.service.DtMatchService;
import pub.module.dating.crud.service.DtMatchmakerService;

/**
 * 牵线申请业务实现
 */
@Service
public class ApiDtMatchServiceImpl implements ApiDtMatchService {

    @Resource
    private ApiDtCustomerService apiDtCustomerService;
    @Resource
    private DtMatchService dtMatchService;
    @Resource
    private DtMatchmakerService dtMatchmakerService;
    @Resource
    private DtContactService dtContactService;
    @Resource
    private ApiDtCusMatchmakerRelService apiDtCusMatchmakerRelService;
    @Resource
    private DatingWxSubscribeNotifyPublisher datingWxSubscribeNotifyPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DtMatchDTO apply(DtMatchDTO body, String applicantUserCode) {
        Assert.notNull(body, "参数不能为空");
        Assert.notBlank(body.getMtMkCode(), "红娘编码不能为空");
        Assert.notBlank(body.getMtPursuedCusCode(), "被追求者客户编码不能为空");
        Assert.notBlank(applicantUserCode, "用户未登录");

        DtCustomerDTO pursuingDto = apiDtCustomerService.getCusByUserCode(applicantUserCode);
        Assert.notNull(pursuingDto, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));
        Assert.notBlank(pursuingDto.getCusCode(), () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));

        String pursuingCusCode = pursuingDto.getCusCode();
        Assert.isFalse(pursuingCusCode.equals(body.getMtPursuedCusCode()), () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));

        DtCustomerDTO pursuedDto = apiDtCustomerService.getCusByCusCode(body.getMtPursuedCusCode());
        Assert.notNull(pursuedDto, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));
        Assert.notBlank(pursuedDto.getCusCode(), () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));

        DtMatchmaker matchmaker = dtMatchmakerService.getByCode(body.getMtMkCode());
        Assert.notNull(matchmaker, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));

        MemberBenefitConsumeResultDTO consumeResult = apiDtCustomerService.tryConsumeMemberBenefit(
                applicantUserCode, CusMemberBenefitTypeEnum.MATCH, 1L, pursuedDto.getCusCode());
        DatingMemberBenefitConsumeUtil.assertConsumed(consumeResult);

        QueryWrapper<DtMatch> pendingMatch = new QueryWrapper<>();
        pendingMatch.lambda()
            .eq(DtMatch::getMtPursuingCusCode, pursuingCusCode)
            .eq(DtMatch::getMtPursuedCusCode, pursuedDto.getCusCode())
            .eq(DtMatch::getMtMkCode, body.getMtMkCode())
            .and(w -> w.isNull(DtMatch::getMtRelationProgressCode)
                .or().ne(DtMatch::getMtRelationProgressCode, MatchRelationProgressCodeEnum.ENDED));
        Assert.isTrue(dtMatchService.count(pendingMatch) == 0, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1003));

        DtMatch row = new DtMatch();
        row.setMtMkCode(body.getMtMkCode());
        row.setMtPursuingCusCode(pursuingCusCode);
        row.setMtPursuingCusName(StrUtil.blankToDefault(pursuingDto.getCusName(), body.getMtPursuingCusName()));
        row.setMtPursuingCusAvatar(firstAvatar(pursuingDto, body.getMtPursuingCusAvatar()));
        row.setMtPursuedCusCode(pursuedDto.getCusCode());
        row.setMtPursuedCusName(StrUtil.blankToDefault(pursuedDto.getCusName(), body.getMtPursuedCusName()));
        row.setMtPursuedCusAvatar(firstAvatar(pursuedDto, body.getMtPursuedCusAvatar()));
        row.setMtName(StrUtil.blankToDefault(body.getMtName(),
            String.format("红娘牵线：%s→%s",
                StrUtil.blankToDefault(row.getMtPursuingCusName(), "我"),
                StrUtil.blankToDefault(row.getMtPursuedCusName(), "Ta"))));
        row.setMtRelationProgressCode(MatchRelationProgressCodeEnum.PENDING_COMMUNICATION);

        dtMatchService.save(row);

        apiDtCusMatchmakerRelService.relateCustomerWithMatchmakerByMkCodeIfAbsent(applicantUserCode, matchmaker.getMkCode());

        datingWxSubscribeNotifyPublisher.publishMatchRequestAfterCommit(row, matchmaker.getMkUserCode());

        return BeanUtil.copyProperties(row, DtMatchDTO.class);
    }

    @Override
    public IPage<DtMatchDTO> listForMatchmaker(String matchmakerUserCode, Integer pageNo, Integer pageSize) {
        Assert.notBlank(matchmakerUserCode, "用户未登录");
        DtMatchmaker matchmaker = dtMatchmakerService.getOne(new QueryWrapper<DtMatchmaker>().lambda()
            .eq(DtMatchmaker::getMkUserCode, matchmakerUserCode.trim()), false);
        Assert.notNull(matchmaker, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));
        Assert.notBlank(matchmaker.getMkCode(), () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));

        int pn = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int ps = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        Page<DtMatch> page = new Page<>(pn, ps);
        QueryWrapper<DtMatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .eq(DtMatch::getMtMkCode, matchmaker.getMkCode())
            .orderByDesc(DtMatch::getCreateTime);
        IPage<DtMatch> pageList = dtMatchService.page(page, queryWrapper);
        return pageList.convert(this::enrichMatchForMatchmaker);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DtMatchDTO updateRelationProgress(DtMatchUpdateRelationProgressVO vo, String matchmakerUserCode) {
        Assert.notNull(vo, "参数不能为空");
        Assert.notBlank(vo.getId(), "牵线记录 id 不能为空");
        Assert.notNull(vo.getMtRelationProgressCode(), "关系进度不能为空");
        Assert.notBlank(matchmakerUserCode, "用户未登录");

        DtMatchmaker matchmaker = dtMatchmakerService.getOne(new QueryWrapper<DtMatchmaker>().lambda()
            .eq(DtMatchmaker::getMkUserCode, matchmakerUserCode.trim()), false);
        Assert.notNull(matchmaker, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));
        Assert.notBlank(matchmaker.getMkCode(), () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));

        DtMatch existing = dtMatchService.getById(vo.getId());
        Assert.notNull(existing, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));
        Assert.isTrue(matchmaker.getMkCode().equals(existing.getMtMkCode()),
            () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1005));

        DtMatch patch = new DtMatch();
        patch.setId(existing.getId());
        patch.setMtRelationProgressCode(vo.getMtRelationProgressCode());
        if (vo.getMtMeetingScreenshot() != null) {
            patch.setMtMeetingScreenshot(StrUtil.trim(vo.getMtMeetingScreenshot()));
        }
        if (vo.getMtChatScreenshot() != null) {
            patch.setMtChatScreenshot(StrUtil.trim(vo.getMtChatScreenshot()));
        }
        dtMatchService.updateById(patch);

        DtMatch updated = dtMatchService.getById(existing.getId());
        return enrichMatchForMatchmaker(updated);
    }

    private DtMatchDTO enrichMatchForMatchmaker(DtMatch row) {
        DtMatchDTO dto = BeanUtil.copyProperties(row, DtMatchDTO.class);
        DtCustomerDTO pursuingDto = apiDtCustomerService.getCusByCusCode(row.getMtPursuingCusCode());
        DtCustomerDTO pursuedDto = apiDtCustomerService.getCusByCusCode(row.getMtPursuedCusCode());
        if (pursuingDto != null) {
            dto.setMtPursuingCusPhone(StrUtil.blankToDefault(pursuingDto.getCusPhone(), dto.getMtPursuingCusPhone()));
            if (StrUtil.isBlank(dto.getMtPursuingCusName())) {
                dto.setMtPursuingCusName(pursuingDto.getCusName());
            }
            if (StrUtil.isBlank(dto.getMtPursuingCusAvatar())) {
                dto.setMtPursuingCusAvatar(firstAvatar(pursuingDto, null));
            }
        }
        if (pursuedDto != null) {
            dto.setMtPursuedCusPhone(StrUtil.blankToDefault(pursuedDto.getCusPhone(), dto.getMtPursuedCusPhone()));
            if (StrUtil.isBlank(dto.getMtPursuedCusName())) {
                dto.setMtPursuedCusName(pursuedDto.getCusName());
            }
            if (StrUtil.isBlank(dto.getMtPursuedCusAvatar())) {
                dto.setMtPursuedCusAvatar(firstAvatar(pursuedDto, null));
            }
        }
        dto.setMtAreFriends(areCustomersFriends(
            pursuingDto != null ? pursuingDto.getCusUserCode() : null,
            pursuedDto != null ? pursuedDto.getCusCode() : null));
        return dto;
    }

    private boolean areCustomersFriends(String pursuingUserCode, String pursuedCusCode) {
        String ownerUserCode = StrUtil.trim(pursuingUserCode);
        String peerCusCode = StrUtil.trim(pursuedCusCode);
        if (StrUtil.isBlank(ownerUserCode) || StrUtil.isBlank(peerCusCode)) {
            return false;
        }
        return dtContactService.count(new QueryWrapper<DtContact>().lambda()
            .eq(DtContact::getUserCode, ownerUserCode)
            .eq(DtContact::getCusCode, peerCusCode)) > 0;
    }

    /**
     * 优先取库里的客户头像；支持逗号分隔多图时取第一张。
     */
    private static String firstAvatar(DtCustomerDTO dto, String fallbackFromRequest) {
        String raw = dto != null ? dto.getCusAvatar() : null;
        if (StrUtil.isNotBlank(raw)) {
            return StrUtil.trim(StrUtil.subBefore(raw, ",", false));
        }
        return fallbackFromRequest;
    }
}

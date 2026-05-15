package pub.module.dating.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.dating.api.constants.DatingErrorCodeEnum;
import pub.module.dating.api.service.ApiDtContactApplyService;
import pub.module.dating.api.service.ApiDtCusMatchmakerRelService;
import pub.module.dating.api.service.ApiDtMatchService;
import pub.module.dating.api.service.dto.DtMatchDTO;
import pub.module.dating.curd.entity.DtContact;
import pub.module.dating.curd.entity.DtContactApply;
import pub.module.dating.curd.entity.DtMatch;
import pub.module.dating.curd.entity.DtMatchmaker;
import pub.module.dating.curd.service.DtContactApplyService;
import pub.module.dating.curd.service.DtContactService;
import pub.module.dating.curd.service.DtMatchService;
import pub.module.dating.curd.service.DtMatchmakerService;

/**
 * 牵线申请业务实现
 */
@Service
public class ApiDtMatchServiceImpl implements ApiDtMatchService {

    /**
     * 与前端 {@code contact.ts} / 列表展示文案「红娘牵线」一致
     */
    public static final String CONTACT_SOURCE_MATCHMAKER_MATCHING = "matchmakerMatching";

    @Resource
    private ApiCustomerService apiCustomerService;
    @Resource
    private ApiDtContactApplyService apiDtContactApplyService;
    @Resource
    private DtMatchService dtMatchService;
    @Resource
    private DtMatchmakerService dtMatchmakerService;
    @Resource
    private DtContactService dtContactService;
    @Resource
    private DtContactApplyService dtContactApplyService;
    @Resource
    private ApiDtCusMatchmakerRelService apiDtCusMatchmakerRelService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DtMatchDTO apply(DtMatchDTO body, String applicantUserCode) {
        Assert.notNull(body, "参数不能为空");
        Assert.notBlank(body.getMtMkCode(), "红娘编码不能为空");
        Assert.notBlank(body.getMtPursuedCusCode(), "被追求者客户编码不能为空");
        Assert.notBlank(applicantUserCode, "用户未登录");

        CustomerDTO pursuingDto = apiCustomerService.getCusByUserCode(applicantUserCode);
        Assert.notNull(pursuingDto, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));
        Assert.notBlank(pursuingDto.getCusCode(), () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));

        String pursuingCusCode = pursuingDto.getCusCode();
        Assert.isFalse(pursuingCusCode.equals(body.getMtPursuedCusCode()), () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));

        CustomerDTO pursuedDto = apiCustomerService.getCusByCusCode(body.getMtPursuedCusCode());
        Assert.notNull(pursuedDto, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));
        Assert.notBlank(pursuedDto.getCusCode(), () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));

        DtMatchmaker matchmaker = dtMatchmakerService.getByCode(body.getMtMkCode());
        Assert.notNull(matchmaker, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1000));

        Long matchQuota = pursuingDto.getCusMatchRightValue();
        Assert.isTrue(matchQuota != null && matchQuota > 0, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1001));

        long friendCount = dtContactService.count(new QueryWrapper<DtContact>().lambda()
            .eq(DtContact::getUserCode, applicantUserCode)
            .eq(DtContact::getCusCode, pursuedDto.getCusCode()));
        Assert.isTrue(friendCount == 0, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1002));

        QueryWrapper<DtContactApply> pendingApply = new QueryWrapper<>();
        pendingApply.lambda()
            .eq(DtContactApply::getUserCode, applicantUserCode)
            .eq(DtContactApply::getCusCode, pursuedDto.getCusCode())
            .and(w -> w.isNull(DtContactApply::getContactApplyPassedStatusCode)
                .or().eq(DtContactApply::getContactApplyPassedStatusCode, "")
                .or().eq(DtContactApply::getContactApplyPassedStatusCode, "0"));
        Assert.isTrue(dtContactApplyService.count(pendingApply) == 0, () -> new pub.module.common.exception.BizException(DatingErrorCodeEnum.E1002));

        QueryWrapper<DtMatch> pendingMatch = new QueryWrapper<>();
        pendingMatch.lambda()
            .eq(DtMatch::getMtPursuingCusCode, pursuingCusCode)
            .eq(DtMatch::getMtPursuedCusCode, pursuedDto.getCusCode())
            .eq(DtMatch::getMtMkCode, body.getMtMkCode())
            .and(w -> w.isNull(DtMatch::getMtPassedStatusCode)
                .or().eq(DtMatch::getMtPassedStatusCode, ""));
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
        row.setMtPassedStatusCode(null);

        dtMatchService.save(row);

        ApiDtContactApplyService.ApplyDTO applyDTO = new ApiDtContactApplyService.ApplyDTO();
        applyDTO.setCusCode(pursuedDto.getCusCode());
        applyDTO.setContactApplyGreeting(buildContactGreeting(row));
        applyDTO.setContactSourceCode(CONTACT_SOURCE_MATCHMAKER_MATCHING);
        apiDtContactApplyService.apply(applyDTO, applicantUserCode);

        apiDtCusMatchmakerRelService.relateCustomerWithMatchmakerByMkCodeIfAbsent(applicantUserCode, matchmaker.getMkCode());

        return BeanUtil.copyProperties(row, DtMatchDTO.class);
    }

    private static String buildContactGreeting(DtMatch row) {
        if (StrUtil.isNotBlank(row.getMtName())) {
            return row.getMtName();
        }
        return "您好，我通过平台红娘牵线希望与您认识，期待您的回复。";
    }

    /**
     * 优先取库里的客户头像；支持逗号分隔多图时取第一张。
     */
    private static String firstAvatar(CustomerDTO dto, String fallbackFromRequest) {
        String raw = dto != null ? dto.getCusAvatar() : null;
        if (StrUtil.isNotBlank(raw)) {
            return StrUtil.trim(StrUtil.subBefore(raw, ",", false));
        }
        return fallbackFromRequest;
    }
}

package pub.module.dating.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.IdentityApplyProcessCodeEnum;
import pub.module.dating.api.constants.MatchmakerTagConstants;
import pub.module.dating.api.service.ApiDtMatchmakerService;
import pub.module.dating.api.service.dto.MatchmakerBriefDTO;
import pub.module.dating.api.service.dto.MatchmakerChannelsDTO;
import pub.module.dating.api.service.dto.MatchmakerQualificationApplyDTO;
import pub.module.dating.api.service.dto.MatchmakerQualificationApplySubmitVO;
import pub.module.dating.api.service.dto.MatchmakingCompanyOptionDTO;
import pub.module.dating.crud.entity.DtCusMatchmakerRel;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.entity.DtMatchmakingCompany;
import pub.module.dating.crud.mapper.DtMatchmakerMapper;
import pub.module.dating.crud.service.DtCusMatchmakerRelService;
import pub.module.dating.crud.service.DtMatchmakerService;
import pub.module.dating.crud.service.DtMatchmakingCompanyService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import java.util.List;

/**
 * Api 红娘信息 Service
 */
@Slf4j
@Service
public class ApiDtMatchmakerServiceImpl implements ApiDtMatchmakerService {

    @Resource
    private DtMatchmakerMapper dtMatchmakerMapper;
    @Resource
    private DtMatchmakerService dtMatchmakerService;
    @Resource
    private ApiSysUserService apiSysUserService;
    @Resource
    private DtMatchmakingCompanyService dtMatchmakingCompanyService;
    @Resource
    private DtCusMatchmakerRelService dtCusMatchmakerRelService;

    @Override
    public void assertMkUserServesCustomer(String mkUserCode, String cusCode) {
        if (StrUtil.isBlank(mkUserCode)) {
            throw new IllegalArgumentException("红娘未登录");
        }
        if (StrUtil.isBlank(cusCode)) {
            throw new IllegalArgumentException("客户编码不能为空");
        }
        DtMatchmaker matchmaker = dtMatchmakerService.getOne(new QueryWrapper<DtMatchmaker>().lambda()
                .eq(DtMatchmaker::getMkUserCode, mkUserCode.trim()), false);
        Assert.notNull(matchmaker, "红娘信息不存在");
        Assert.notBlank(matchmaker.getMkCode(), "红娘编码不存在");
        long count = dtCusMatchmakerRelService.count(new QueryWrapper<DtCusMatchmakerRel>().lambda()
                .eq(DtCusMatchmakerRel::getMkCode, matchmaker.getMkCode())
                .eq(DtCusMatchmakerRel::getCusCode, cusCode.trim()));
        if (count <= 0) {
            throw new IllegalArgumentException("该客户不在您的服务范围内");
        }
    }

    @Override
    public MatchmakerBriefDTO getMatchmakerBriefByUserCode(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return null;
        }
        DtMatchmaker matchmaker = dtMatchmakerService.getOne(new QueryWrapper<DtMatchmaker>().lambda()
                .eq(DtMatchmaker::getMkUserCode, userCode.trim()), false);
        if (matchmaker == null) {
            return null;
        }
        MatchmakerBriefDTO dto = new MatchmakerBriefDTO();
        dto.setMkCode(matchmaker.getMkCode());
        dto.setMkName(matchmaker.getMkName());
        dto.setMkUserCode(matchmaker.getMkUserCode());
        return dto;
    }

    @Override
    public boolean isMatchmakerByUserCode(String userCode) {
        if (userCode == null || userCode.isBlank()) {
            return false;
        }
        return dtMatchmakerMapper.selectCount(new QueryWrapper<DtMatchmaker>().lambda()
                .eq(DtMatchmaker::getMkUserCode, userCode)) > 0;
    }

    @Override
    public void syncUserRealNameFromMatchmaker(String userCode, String mkName) {
        if (StrUtil.isBlank(userCode) || StrUtil.isBlank(mkName)) {
            return;
        }
        String normalizedCode = userCode.trim();
        String normalizedName = mkName.trim();
        UserDTO user = apiSysUserService.getUserByUserCode(normalizedCode);
        if (user == null) {
            return;
        }
        if (normalizedName.equals(StrUtil.trim(user.getUserRealName()))) {
            return;
        }
        apiSysUserService.updateUserRealNameByUserCode(normalizedCode, normalizedName);
        log.info("已同步用户真实姓名 userCode={} userRealName={}", normalizedCode, normalizedName);
    }

    @Override
    public List<MatchmakingCompanyOptionDTO> listCertifiedCompanies() {
        List<DtMatchmakingCompany> companies = dtMatchmakingCompanyService.list(new QueryWrapper<DtMatchmakingCompany>().lambda()
                .eq(DtMatchmakingCompany::getMkCompanyIdentityStatusCode, StatusCodeEnum.YES)
                .orderByAsc(DtMatchmakingCompany::getMkCompanyName));
        return companies.stream().map(company -> {
            MatchmakingCompanyOptionDTO dto = new MatchmakingCompanyOptionDTO();
            dto.setMkCompanyCode(company.getMkCompanyCode());
            dto.setMkCompanyName(company.getMkCompanyName());
            return dto;
        }).toList();
    }

    @Override
    public MatchmakerQualificationApplyDTO getMyQualificationApply(String userCode) {
        Assert.notBlank(userCode, "请先登录");
        return toApplyDto(dtMatchmakerService.getByUserCode(userCode));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MatchmakerQualificationApplyDTO submitQualificationApply(String userCode, MatchmakerQualificationApplySubmitVO vo) {
        Assert.notBlank(userCode, "请先登录");
        validateSubmit(vo);

        DtMatchmaker existing = dtMatchmakerService.getByUserCode(userCode);
        if (existing != null && StatusCodeEnum.isYesValue(existing.getMkIdentityStatusCode())) {
            throw new IllegalArgumentException("红娘资质已通过审核，无需重复提交");
        }
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(existing != null
                ? existing.getMkIdentityProcessCode() : null);
        if (existing != null && process == IdentityApplyProcessCodeEnum.REVIEWING) {
            throw new IllegalArgumentException("申请审核中，请耐心等待");
        }
        if (existing != null && process == IdentityApplyProcessCodeEnum.PLATFORM_REVIEWING) {
            throw new IllegalArgumentException("平台审核中，请耐心等待");
        }

        DtMatchmaker entity = existing != null ? existing : new DtMatchmaker();
        if (existing == null) {
            entity.setMkUserCode(userCode.trim());
            entity.setMkIdentityStatusCode(StatusCodeEnum.NO);
            entity.setMkIdentityProcessCode(IdentityApplyProcessCodeEnum.DRAFT);
        }
        copySubmitFields(entity, vo);
        entity.setMkIdentityStatusCode(StatusCodeEnum.NO);
        entity.setMkIdentityProcessCode(IdentityApplyProcessCodeEnum.REVIEWING);
        entity.setMkIdentityRejectReason(null);
        entity.setMkPlatformRejectReason(null);
        entity.setMkVideoCommitmentFile(null);
        entity.setMkServiceAgreementFile(null);
        applyChannelsAuditStatus(entity, existing);

        if (existing == null) {
            dtMatchmakerService.save(entity);
        }
        else {
            dtMatchmakerService.updateById(entity);
        }

        syncUserRealNameFromMatchmaker(userCode, entity.getMkName());
        // 申请注册红娘的用户自动增加“红娘”用户标签
        apiSysUserService.addUserTag(userCode.trim(),
                MatchmakerTagConstants.MATCHMAKER_TAG_CODE, MatchmakerTagConstants.MATCHMAKER_TAG_NAME);
        return toApplyDto(entity);
    }

    private void validateSubmit(MatchmakerQualificationApplySubmitVO vo) {
        Assert.notNull(vo, "请填写申请信息");
        Assert.notBlank(vo.getMkWorkPhoto(), "请上传工作照");
        Assert.notBlank(vo.getMkName(), "请填写红娘姓名");
        Assert.notNull(vo.getMkAge(), "请填写年龄");
        Assert.isTrue(vo.getMkAge() > 0 && vo.getMkAge() < 100, "请填写有效年龄");
        Assert.notBlank(vo.getMkPhone(), "请填写联系电话");
        Assert.notBlank(vo.getMkIdNo(), "请填写证件号");
        Assert.notBlank(vo.getMkCompanyCode(), "请选择所属婚介公司");
        Assert.notBlank(vo.getMkCompanyName(), "请选择所属婚介公司");
        Assert.notBlank(vo.getMkCityCode(), "请选择所在城市");
        Assert.notBlank(vo.getMkCityName(), "请选择所在城市");
        Assert.notBlank(vo.getMkMoment(), "请填写红娘说说");
        Assert.notBlank(vo.getMkTags(), "请选择擅长");
        validateCertifiedCompany(vo.getMkCompanyCode(), vo.getMkCompanyName());
    }

    private void validateCertifiedCompany(String companyCode, String companyName) {
        DtMatchmakingCompany company = dtMatchmakingCompanyService.getByCode(trim(companyCode));
        Assert.notNull(company, "请选择有效的婚介公司");
        Assert.isTrue(StatusCodeEnum.isYesValue(company.getMkCompanyIdentityStatusCode()), "请选择已审核通过的婚介公司");
        Assert.isTrue(trim(companyName).equals(trim(company.getMkCompanyName())), "婚介公司信息不匹配");
    }

    private void copySubmitFields(DtMatchmaker entity, MatchmakerQualificationApplySubmitVO vo) {
        entity.setMkWorkPhoto(trim(vo.getMkWorkPhoto()));
        entity.setMkName(trim(vo.getMkName()));
        entity.setMkAge(vo.getMkAge());
        entity.setMkPhone(trim(vo.getMkPhone()));
        entity.setMkIdNo(trim(vo.getMkIdNo()));
        entity.setMkCompanyCode(trim(vo.getMkCompanyCode()));
        entity.setMkCompanyName(trim(vo.getMkCompanyName()));
        entity.setMkCityCode(trim(vo.getMkCityCode()));
        entity.setMkCityName(trim(vo.getMkCityName()));
        entity.setMkMoment(trim(vo.getMkMoment()));
        entity.setMkTags(trim(vo.getMkTags()));
        entity.setMkChannelsFinderUserName(trim(vo.getMkChannelsFinderUserName()));
    }

    private static void applyChannelsAuditStatus(DtMatchmaker entity, DtMatchmaker before) {
        String finder = trim(entity.getMkChannelsFinderUserName());
        if (StrUtil.isBlank(finder) || !finder.startsWith("sph")) {
            entity.setMkChannelsAuditStatusCode(StatusCodeEnum.NO);
            entity.setMkChannelsProcessCode(null);
            entity.setMkChannelsRejectReason(null);
            entity.setMkChannelsAuditBy(null);
            entity.setMkChannelsAuditAt(null);
            return;
        }
        if (before != null
                && finder.equals(trim(before.getMkChannelsFinderUserName()))
                && StatusCodeEnum.isYesValue(before.getMkChannelsAuditStatusCode())
                && IdentityApplyProcessCodeEnum.APPROVED == IdentityApplyProcessCodeEnum.effective(before.getMkChannelsProcessCode())) {
            entity.setMkChannelsAuditStatusCode(before.getMkChannelsAuditStatusCode());
            entity.setMkChannelsProcessCode(before.getMkChannelsProcessCode());
            entity.setMkChannelsRejectReason(before.getMkChannelsRejectReason());
            entity.setMkChannelsAuditBy(before.getMkChannelsAuditBy());
            entity.setMkChannelsAuditAt(before.getMkChannelsAuditAt());
            return;
        }
        entity.setMkChannelsAuditStatusCode(StatusCodeEnum.NO);
        entity.setMkChannelsProcessCode(IdentityApplyProcessCodeEnum.DRAFT);
        entity.setMkChannelsRejectReason(null);
        entity.setMkChannelsAuditBy(null);
        entity.setMkChannelsAuditAt(null);
    }

    private MatchmakerQualificationApplyDTO toApplyDto(DtMatchmaker matchmaker) {
        MatchmakerQualificationApplyDTO dto = new MatchmakerQualificationApplyDTO();
        if (matchmaker == null) {
            dto.setHasRecord(false);
            dto.setMkIdentityStatusCode(StatusCodeEnum.NO.getCode());
            dto.setMkIdentityProcessCode(IdentityApplyProcessCodeEnum.DRAFT.getCode());
            fillProcessFlags(dto, null, StatusCodeEnum.NO);
            return dto;
        }

        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(matchmaker.getMkIdentityProcessCode());
        StatusCodeEnum status = matchmaker.getMkIdentityStatusCode();

        dto.setHasRecord(true);
        dto.setMkCode(matchmaker.getMkCode());
        dto.setMkWorkPhoto(matchmaker.getMkWorkPhoto());
        dto.setMkName(matchmaker.getMkName());
        dto.setMkAge(matchmaker.getMkAge());
        dto.setMkPhone(matchmaker.getMkPhone());
        dto.setMkIdNo(matchmaker.getMkIdNo());
        dto.setMkCompanyCode(matchmaker.getMkCompanyCode());
        dto.setMkCompanyName(matchmaker.getMkCompanyName());
        dto.setMkCityCode(matchmaker.getMkCityCode());
        dto.setMkCityName(matchmaker.getMkCityName());
        dto.setMkMoment(matchmaker.getMkMoment());
        dto.setMkTags(matchmaker.getMkTags());
        dto.setMkChannelsFinderUserName(matchmaker.getMkChannelsFinderUserName());
        dto.setMkIdentityStatusCode(statusCodeValue(status));
        dto.setMkIdentityProcessCode(process.getCode());
        fillProcessFlags(dto, process, status);
        return dto;
    }

    private static void fillProcessFlags(MatchmakerQualificationApplyDTO dto,
                                         IdentityApplyProcessCodeEnum process,
                                         StatusCodeEnum status) {
        IdentityApplyProcessCodeEnum p = IdentityApplyProcessCodeEnum.effective(process);
        dto.setCertified(isMatchmakerCertified(p, status));
        dto.setPending(p == IdentityApplyProcessCodeEnum.REVIEWING || p == IdentityApplyProcessCodeEnum.PLATFORM_REVIEWING);
        dto.setRejected(p == IdentityApplyProcessCodeEnum.REJECTED);
        dto.setDraft(p == IdentityApplyProcessCodeEnum.DRAFT);
        dto.setAuditStatusLabel(auditStatusLabel(p));
    }

    private static String auditStatusLabel(IdentityApplyProcessCodeEnum process) {
        if (process == IdentityApplyProcessCodeEnum.PLATFORM_REVIEWING) {
            return "平台审核中";
        }
        return IdentityApplyProcessCodeEnum.label(process);
    }

    private static String statusCodeValue(StatusCodeEnum status) {
        return StatusCodeEnum.isYesValue(status) ? StatusCodeEnum.YES.getCode() : StatusCodeEnum.NO.getCode();
    }

    private static String trim(String value) {
        return StrUtil.trim(value);
    }

    @Override
    public MatchmakerChannelsDTO getMyChannels(String userCode) {
        return toChannelsDto(requireCertifiedMatchmaker(userCode));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MatchmakerChannelsDTO updateMyChannels(String userCode, String mkChannelsFinderUserName) {
        DtMatchmaker matchmaker = requireCertifiedMatchmaker(userCode);
        String finder = trim(mkChannelsFinderUserName);
        if (StrUtil.isNotBlank(finder) && !finder.startsWith("sph")) {
            throw new IllegalArgumentException("视频号 ID 需以 sph 开头");
        }
        if (StrUtil.isBlank(finder)) {
            matchmaker.setMkChannelsFinderUserName(null);
            matchmaker.setMkChannelsAuditStatusCode(StatusCodeEnum.NO);
            matchmaker.setMkChannelsProcessCode(null);
            matchmaker.setMkChannelsRejectReason(null);
            matchmaker.setMkChannelsAuditBy(null);
            matchmaker.setMkChannelsAuditAt(null);
        }
        else {
            matchmaker.setMkChannelsFinderUserName(finder);
            matchmaker.setMkChannelsAuditStatusCode(StatusCodeEnum.NO);
            matchmaker.setMkChannelsProcessCode(IdentityApplyProcessCodeEnum.DRAFT);
            matchmaker.setMkChannelsRejectReason(null);
            matchmaker.setMkChannelsAuditBy(null);
            matchmaker.setMkChannelsAuditAt(null);
        }
        dtMatchmakerService.updateById(matchmaker);
        return toChannelsDto(matchmaker);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MatchmakerChannelsDTO submitMyChannels(String userCode) {
        DtMatchmaker matchmaker = requireCertifiedMatchmaker(userCode);
        String finder = trim(matchmaker.getMkChannelsFinderUserName());
        if (StrUtil.isBlank(finder)) {
            throw new IllegalArgumentException("请先填写并保存视频号 ID");
        }
        if (!finder.startsWith("sph")) {
            throw new IllegalArgumentException("视频号 ID 需以 sph 开头");
        }
        IdentityApplyProcessCodeEnum process = effectiveChannelsProcess(matchmaker);
        if (!IdentityApplyProcessCodeEnum.canSubmit(process)) {
            throw new IllegalArgumentException("当前状态不可提交审核");
        }
        matchmaker.setMkChannelsProcessCode(IdentityApplyProcessCodeEnum.REVIEWING);
        matchmaker.setMkChannelsRejectReason(null);
        dtMatchmakerService.updateById(matchmaker);
        return toChannelsDto(matchmaker);
    }

    private DtMatchmaker requireCertifiedMatchmaker(String userCode) {
        Assert.notBlank(userCode, "请先登录");
        DtMatchmaker matchmaker = dtMatchmakerService.getByUserCode(userCode);
        Assert.notNull(matchmaker, "未找到红娘信息，请先提交资质申请");
        if (!isMatchmakerCertified(matchmaker)) {
            IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(matchmaker.getMkIdentityProcessCode());
            if (process == IdentityApplyProcessCodeEnum.PLATFORM_REVIEWING) {
                throw new IllegalArgumentException("平台审核中，请耐心等待审核结果");
            }
            if (process == IdentityApplyProcessCodeEnum.REVIEWING) {
                throw new IllegalArgumentException("资质审核中，请耐心等待审核结果");
            }
            throw new IllegalArgumentException("请先完成红娘资质认证");
        }
        if (!StatusCodeEnum.isYesValue(matchmaker.getMkIdentityStatusCode())) {
            matchmaker.setMkIdentityStatusCode(StatusCodeEnum.YES);
            dtMatchmakerService.updateById(matchmaker);
        }
        return matchmaker;
    }

    private static boolean isMatchmakerCertified(DtMatchmaker matchmaker) {
        if (matchmaker == null) {
            return false;
        }
        return isMatchmakerCertified(
                IdentityApplyProcessCodeEnum.effective(matchmaker.getMkIdentityProcessCode()),
                matchmaker.getMkIdentityStatusCode());
    }

    private static boolean isMatchmakerCertified(IdentityApplyProcessCodeEnum process, StatusCodeEnum status) {
        if (StatusCodeEnum.isYesValue(status)) {
            return true;
        }
        return IdentityApplyProcessCodeEnum.effective(process) == IdentityApplyProcessCodeEnum.APPROVED;
    }

    private MatchmakerChannelsDTO toChannelsDto(DtMatchmaker matchmaker) {
        MatchmakerChannelsDTO dto = new MatchmakerChannelsDTO();
        if (matchmaker == null) {
            dto.setStatusLabel("未配置");
            dto.setProcessStatusLabel("—");
            return dto;
        }
        String finder = trim(matchmaker.getMkChannelsFinderUserName());
        IdentityApplyProcessCodeEnum process = effectiveChannelsProcess(matchmaker);
        boolean enabled = StrUtil.isNotBlank(finder)
                && finder.startsWith("sph")
                && StatusCodeEnum.isYesValue(matchmaker.getMkChannelsAuditStatusCode());
        dto.setMkCode(matchmaker.getMkCode());
        dto.setMkChannelsFinderUserName(finder);
        dto.setMkChannelsAuditStatusCode(statusCodeValue(matchmaker.getMkChannelsAuditStatusCode()));
        dto.setMkChannelsProcessCode(process.getCode());
        dto.setMkChannelsRejectReason(trim(matchmaker.getMkChannelsRejectReason()));
        dto.setChannelsEnabled(enabled);
        dto.setCanSubmit(StrUtil.isNotBlank(finder) && IdentityApplyProcessCodeEnum.canSubmit(process));
        if (StrUtil.isBlank(finder)) {
            dto.setStatusLabel("未配置");
            dto.setProcessStatusLabel("—");
        }
        else if (enabled) {
            dto.setStatusLabel("已生效，小程序主页可展示视频号入口");
            dto.setProcessStatusLabel(channelsProcessLabel(process));
        }
        else {
            dto.setStatusLabel("未生效");
            dto.setProcessStatusLabel(channelsProcessLabel(process));
        }
        return dto;
    }

    private static IdentityApplyProcessCodeEnum effectiveChannelsProcess(DtMatchmaker matchmaker) {
        if (matchmaker.getMkChannelsProcessCode() != null) {
            return IdentityApplyProcessCodeEnum.effective(matchmaker.getMkChannelsProcessCode());
        }
        if (StatusCodeEnum.isYesValue(matchmaker.getMkChannelsAuditStatusCode())
                && StrUtil.isNotBlank(trim(matchmaker.getMkChannelsFinderUserName()))) {
            return IdentityApplyProcessCodeEnum.APPROVED;
        }
        if (StrUtil.isNotBlank(trim(matchmaker.getMkChannelsFinderUserName()))) {
            return IdentityApplyProcessCodeEnum.DRAFT;
        }
        return IdentityApplyProcessCodeEnum.DRAFT;
    }

    private static String channelsProcessLabel(IdentityApplyProcessCodeEnum process) {
        IdentityApplyProcessCodeEnum p = IdentityApplyProcessCodeEnum.effective(process);
        return switch (p) {
            case DRAFT -> "待提交";
            case REVIEWING -> "待审核";
            case APPROVED -> "审核通过";
            case REJECTED -> "审核失败";
            default -> p.getDesc();
        };
    }
}

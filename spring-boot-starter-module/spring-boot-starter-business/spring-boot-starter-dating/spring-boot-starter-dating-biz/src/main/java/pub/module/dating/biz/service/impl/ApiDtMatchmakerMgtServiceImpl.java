package pub.module.dating.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.IdentityApplyProcessCodeEnum;
import pub.module.dating.api.service.ApiDtMatchmakerMgtService;
import pub.module.dating.api.service.ApiDtMatchmakerService;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.service.DtMatchmakerService;

import java.time.LocalDateTime;

/**
 * 红娘资质审核：企业审核 + 平台审核。
 */
@Service
public class ApiDtMatchmakerMgtServiceImpl implements ApiDtMatchmakerMgtService {

    @Resource
    private DtMatchmakerService dtMatchmakerService;
    @Resource
    private ApiDtMatchmakerService apiDtMatchmakerService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveByEnterprise(String id, String auditBy, String videoCommitmentFile, String serviceAgreementFile) {
        DtMatchmaker matchmaker = requireForAudit(id);
        if (StatusCodeEnum.isYesValue(matchmaker.getMkIdentityStatusCode())) {
            throw new IllegalArgumentException("该红娘已通过资质认证");
        }
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(matchmaker.getMkIdentityProcessCode());
        if (process != IdentityApplyProcessCodeEnum.REVIEWING && process != IdentityApplyProcessCodeEnum.REJECTED) {
            throw new IllegalArgumentException("当前状态不可进行企业审核");
        }
        if (StrUtil.isBlank(videoCommitmentFile)) {
            throw new IllegalArgumentException("请上传视频承诺文件");
        }
        if (StrUtil.isBlank(serviceAgreementFile)) {
            throw new IllegalArgumentException("请上传红娘服务协议文件");
        }
        LocalDateTime now = LocalDateTime.now();
        matchmaker.setMkVideoCommitmentFile(StrUtil.trim(videoCommitmentFile));
        matchmaker.setMkServiceAgreementFile(StrUtil.trim(serviceAgreementFile));
        matchmaker.setMkIdentityStatusCode(StatusCodeEnum.NO);
        matchmaker.setMkIdentityProcessCode(IdentityApplyProcessCodeEnum.PLATFORM_REVIEWING);
        matchmaker.setMkIdentityRejectReason(null);
        matchmaker.setMkPlatformRejectReason(null);
        matchmaker.setMkEnterpriseAuditBy(StrUtil.trim(auditBy));
        matchmaker.setMkEnterpriseAuditAt(now);
        dtMatchmakerService.updateById(matchmaker);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectByEnterprise(String id, String rejectReason, String auditBy) {
        DtMatchmaker matchmaker = requireForAudit(id);
        if (StatusCodeEnum.isYesValue(matchmaker.getMkIdentityStatusCode())) {
            throw new IllegalArgumentException("已通过认证的红娘不可驳回");
        }
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(matchmaker.getMkIdentityProcessCode());
        if (process != IdentityApplyProcessCodeEnum.REVIEWING && process != IdentityApplyProcessCodeEnum.REJECTED) {
            throw new IllegalArgumentException("当前状态不可进行企业审核");
        }
        if (StrUtil.isBlank(rejectReason)) {
            throw new IllegalArgumentException("请填写驳回原因");
        }
        LocalDateTime now = LocalDateTime.now();
        matchmaker.setMkIdentityStatusCode(StatusCodeEnum.NO);
        matchmaker.setMkIdentityProcessCode(IdentityApplyProcessCodeEnum.REJECTED);
        matchmaker.setMkIdentityRejectReason(StrUtil.trim(rejectReason));
        matchmaker.setMkEnterpriseAuditBy(StrUtil.trim(auditBy));
        matchmaker.setMkEnterpriseAuditAt(now);
        dtMatchmakerService.updateById(matchmaker);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(String id, String auditBy) {
        DtMatchmaker matchmaker = requireForAudit(id);
        if (StatusCodeEnum.isYesValue(matchmaker.getMkIdentityStatusCode())) {
            throw new IllegalArgumentException("该红娘已通过资质认证");
        }
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(matchmaker.getMkIdentityProcessCode());
        if (process != IdentityApplyProcessCodeEnum.PLATFORM_REVIEWING) {
            throw new IllegalArgumentException("仅平台审核中的红娘可操作");
        }
        LocalDateTime now = LocalDateTime.now();
        matchmaker.setMkIdentityStatusCode(StatusCodeEnum.YES);
        matchmaker.setMkIdentityProcessCode(IdentityApplyProcessCodeEnum.APPROVED);
        matchmaker.setMkIdentityRejectReason(null);
        matchmaker.setMkPlatformRejectReason(null);
        matchmaker.setMkIdentityAuditBy(StrUtil.trim(auditBy));
        matchmaker.setMkIdentityAuditAt(now);
        matchmaker.setMkPlatformAuditBy(StrUtil.trim(auditBy));
        matchmaker.setMkPlatformAuditAt(now);
        dtMatchmakerService.updateById(matchmaker);
        apiDtMatchmakerService.syncUserRealNameFromMatchmaker(matchmaker.getMkUserCode(), matchmaker.getMkName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void directApprove(String id, String auditBy) {
        DtMatchmaker matchmaker = requireForAudit(id);
        if (StatusCodeEnum.isYesValue(matchmaker.getMkIdentityStatusCode())) {
            throw new IllegalArgumentException("该红娘已通过资质认证");
        }
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(matchmaker.getMkIdentityProcessCode());
        if (process == IdentityApplyProcessCodeEnum.APPROVED) {
            throw new IllegalArgumentException("该红娘已通过审核");
        }
        LocalDateTime now = LocalDateTime.now();
        String auditor = StrUtil.trim(auditBy);
        matchmaker.setMkIdentityStatusCode(StatusCodeEnum.YES);
        matchmaker.setMkIdentityProcessCode(IdentityApplyProcessCodeEnum.APPROVED);
        matchmaker.setMkIdentityRejectReason(null);
        matchmaker.setMkPlatformRejectReason(null);
        matchmaker.setMkIdentityAuditBy(auditor);
        matchmaker.setMkIdentityAuditAt(now);
        matchmaker.setMkPlatformAuditBy(auditor);
        matchmaker.setMkPlatformAuditAt(now);
        if (process == IdentityApplyProcessCodeEnum.REVIEWING
                || process == IdentityApplyProcessCodeEnum.REJECTED
                || process == IdentityApplyProcessCodeEnum.DRAFT) {
            matchmaker.setMkEnterpriseAuditBy(auditor);
            matchmaker.setMkEnterpriseAuditAt(now);
        }
        dtMatchmakerService.updateById(matchmaker);
        apiDtMatchmakerService.syncUserRealNameFromMatchmaker(matchmaker.getMkUserCode(), matchmaker.getMkName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(String id, String rejectReason, String auditBy) {
        DtMatchmaker matchmaker = requireForAudit(id);
        if (StatusCodeEnum.isYesValue(matchmaker.getMkIdentityStatusCode())) {
            throw new IllegalArgumentException("已通过认证的红娘不可驳回");
        }
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(matchmaker.getMkIdentityProcessCode());
        if (process != IdentityApplyProcessCodeEnum.PLATFORM_REVIEWING) {
            throw new IllegalArgumentException("仅平台审核中的红娘可操作");
        }
        if (StrUtil.isBlank(rejectReason)) {
            throw new IllegalArgumentException("请填写驳回原因");
        }
        LocalDateTime now = LocalDateTime.now();
        matchmaker.setMkIdentityStatusCode(StatusCodeEnum.NO);
        matchmaker.setMkIdentityProcessCode(IdentityApplyProcessCodeEnum.REJECTED);
        matchmaker.setMkPlatformRejectReason(StrUtil.trim(rejectReason));
        matchmaker.setMkPlatformAuditBy(StrUtil.trim(auditBy));
        matchmaker.setMkPlatformAuditAt(now);
        dtMatchmakerService.updateById(matchmaker);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveChannels(String id, String auditBy) {
        DtMatchmaker matchmaker = requireForAudit(id);
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(matchmaker.getMkChannelsProcessCode());
        if (process != IdentityApplyProcessCodeEnum.REVIEWING) {
            throw new IllegalArgumentException("仅待审核的视频号配置可操作");
        }
        String finder = StrUtil.trim(matchmaker.getMkChannelsFinderUserName());
        if (StrUtil.isBlank(finder) || !finder.startsWith("sph")) {
            throw new IllegalArgumentException("视频号 ID 无效");
        }
        LocalDateTime now = LocalDateTime.now();
        matchmaker.setMkChannelsAuditStatusCode(StatusCodeEnum.YES);
        matchmaker.setMkChannelsProcessCode(IdentityApplyProcessCodeEnum.APPROVED);
        matchmaker.setMkChannelsRejectReason(null);
        matchmaker.setMkChannelsAuditBy(StrUtil.trim(auditBy));
        matchmaker.setMkChannelsAuditAt(now);
        dtMatchmakerService.updateById(matchmaker);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectChannels(String id, String rejectReason, String auditBy) {
        DtMatchmaker matchmaker = requireForAudit(id);
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(matchmaker.getMkChannelsProcessCode());
        if (process != IdentityApplyProcessCodeEnum.REVIEWING) {
            throw new IllegalArgumentException("仅待审核的视频号配置可操作");
        }
        if (StrUtil.isBlank(rejectReason)) {
            throw new IllegalArgumentException("请填写审核失败原因");
        }
        LocalDateTime now = LocalDateTime.now();
        matchmaker.setMkChannelsAuditStatusCode(StatusCodeEnum.NO);
        matchmaker.setMkChannelsProcessCode(IdentityApplyProcessCodeEnum.REJECTED);
        matchmaker.setMkChannelsRejectReason(StrUtil.trim(rejectReason));
        matchmaker.setMkChannelsAuditBy(StrUtil.trim(auditBy));
        matchmaker.setMkChannelsAuditAt(now);
        dtMatchmakerService.updateById(matchmaker);
    }

    private DtMatchmaker requireForAudit(String id) {
        if (StrUtil.isBlank(id)) {
            throw new IllegalArgumentException("红娘 id 不能为空");
        }
        DtMatchmaker matchmaker = dtMatchmakerService.getById(id);
        if (matchmaker == null) {
            throw new IllegalArgumentException("红娘不存在");
        }
        return matchmaker;
    }
}

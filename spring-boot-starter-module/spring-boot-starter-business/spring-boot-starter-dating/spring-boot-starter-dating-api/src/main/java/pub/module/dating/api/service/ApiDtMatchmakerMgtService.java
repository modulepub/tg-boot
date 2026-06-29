package pub.module.dating.api.service;

/**
 * 管理端-红娘资质审核（含代审）。
 */
public interface ApiDtMatchmakerMgtService {

    /** 审核通过（平台审核：仅平台审核中可操作） */
    void approve(String id, String auditBy);

    /** 审核驳回（平台审核：仅平台审核中可操作） */
    void reject(String id, String rejectReason, String auditBy);

    /** 企业审核通过（上传附件后进入平台审核） */
    void approveByEnterprise(String id, String auditBy, String videoCommitmentFile, String serviceAgreementFile);

    /** 企业审核驳回 */
    void rejectByEnterprise(String id, String rejectReason, String auditBy);

    /** 直接通过（跳过企业/平台审核流程） */
    void directApprove(String id, String auditBy);

    /** 视频号审核通过（仅待审核可操作） */
    void approveChannels(String id, String auditBy);

    /** 视频号审核驳回（仅待审核可操作） */
    void rejectChannels(String id, String rejectReason, String auditBy);
}

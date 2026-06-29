package pub.module.verification.api.service;

/**
 * 管理端-资产认证审核
 */
public interface ApiVtAssetCertMgtService {

    /** 审核通过 */
    void approve(String id, String auditBy);

    /** 审核驳回 */
    void reject(String id, String rejectReason, String auditBy);
}

package pub.module.verification.api.service;

/**
 * 管理端-内容合法校验人工审核
 */
public interface ApiContentModerationMgtService {

    /** 人工审核通过 */
    void approve(String id, String auditBy);

    /** 人工审核驳回 */
    void reject(String id, String rejectReason, String auditBy);
}

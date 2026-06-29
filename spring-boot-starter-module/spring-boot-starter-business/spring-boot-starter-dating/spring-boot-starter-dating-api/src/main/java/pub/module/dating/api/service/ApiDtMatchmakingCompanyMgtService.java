package pub.module.dating.api.service;

import pub.module.dating.api.service.dto.MatchmakingCompanyMgtEditVO;

/**
 * 管理端-婚介公司入驻审核。
 */
public interface ApiDtMatchmakingCompanyMgtService {

    /** 审核通过 */
    void approve(String id, String auditBy);

    /** 审核驳回 */
    void reject(String id, String rejectReason, String auditBy);

    /** 代提交审核（待提交/已驳回 → 审核中） */
    void submitForReview(String id);

    /** 编辑入驻资料（待提交/已驳回） */
    void updateApplyInfo(MatchmakingCompanyMgtEditVO vo);

    /** 设置企业管理员 */
    void setAdmin(String id, String adminUserCode);
}

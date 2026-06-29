package pub.module.dating.api.service;

import pub.module.dating.api.service.dto.DtCustomerDTO;

/**
 * 客户资料变更后，同步交友模块各表冗余快照。
 */
public interface ApiCustomerRedundantSyncService {

    /**
     * @param userCode 当前登录用户编码（{@code DtCustomer.cus_user_code}，必填）
     * @param DtCustomer 已落库的最新客户资料
     */
    void syncAfterProfileUpdated(String userCode, DtCustomerDTO customer);

    /**
     * 仅同步冗余快照（推荐/喜欢/联系人/红娘关联等），不触发免费推荐、互选刷新及订阅通知。
     */
    void syncSnapshotAfterProfileUpdated(String userCode, DtCustomerDTO customer);
}

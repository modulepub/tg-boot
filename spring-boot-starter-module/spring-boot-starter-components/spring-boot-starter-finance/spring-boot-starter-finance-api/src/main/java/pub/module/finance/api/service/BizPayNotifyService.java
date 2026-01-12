package pub.module.finance.api.service;

import pub.module.finance.curd.entity.FcAccountLog;

/**
 * 支付
 */


public interface BizPayNotifyService {

    /**
     * 支付回调
     * @param fcAccountLog 流水实体
     */
    void payCallBack(FcAccountLog fcAccountLog);
}

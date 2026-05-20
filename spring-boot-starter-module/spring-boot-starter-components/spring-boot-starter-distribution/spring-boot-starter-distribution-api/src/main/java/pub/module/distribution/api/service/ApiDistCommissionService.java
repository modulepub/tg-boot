package pub.module.distribution.api.service;

import pub.module.distribution.api.service.dto.DistOrderPaidNotifyDTO;

/**
 * 分销分佣核心 API（不依赖 trade 模块）。
 */
public interface ApiDistCommissionService {

    /**
     * 订单商品支付成功后计提分佣。
     */
    void onOrderGoodsPaid(DistOrderPaidNotifyDTO notify);

    /**
     * 服务期到期批量结算。
     *
     * @return 本次结算笔数
     */
    int settleDueServicePeriods();
}

package pub.module.trade.api.service;

import pub.module.trade.api.dto.TdOrderGoodsDTO;

/**
 * 订单商品支付成功后的分销分佣回调（由 distribution-trade-plugin 等实现）。
 */
public interface SpiDistCommissionOnPaidService {

    void onOrderGoodsPaid(TdOrderGoodsDTO tdOrderGoodsDTO);
}

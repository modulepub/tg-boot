package pub.module.trade.api.service;

import pub.module.trade.api.dto.TdOrderGoodsDTO;

/**
 * td_order
 * @author tg
 * @since 2025-12-09
 * @version V1.0
 */
public interface SpiNotifyThirdPaidResultService {
     void notify(TdOrderGoodsDTO tdOrderGoodsDTO);
}

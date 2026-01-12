package pub.module.trade.biz.service;

import pub.module.trade.curd.entity.TdOrder;
import pub.module.trade.curd.entity.TdOrderGoods;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单业务服务接口
 * 定义订单业务操作接口
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
public interface BizTradeOrderService {
    TdOrder createOrder(List<TdOrderGoods> tdOrderGoodsList, String tdOdUserCode, String tdOdUserRealName, String tdOdUserPhone );
    TdOrder queryOrderByCode(String tdOdCode);
    TdOrder paidOrder(BigDecimal validateAmount, String tdOdCode);
}

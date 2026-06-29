package pub.module.trade.biz.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pub.module.trade.crud.entity.TdOrder;
import pub.module.trade.crud.entity.TdOrderGoods;

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
    @Schema(title ="下单")
    @Data
    class OrderGoodsDTO{
        /**编码*/
        @Schema(description = "编码")
        private java.lang.String tdGdCode;
        /**下单数量*/
        @Schema(description = "下单数量")
        private java.math.BigDecimal tdOdGdNum;
    }
    TdOrder createOrder(List<OrderGoodsDTO> tdOrderGoodsList, String tdOdUserCode, String tdOdUserRealName, String tdOdUserPhone );
    TdOrder queryOrderByCode(String tdOdCode);
    TdOrder paidOrder(String tdOdCode);
}

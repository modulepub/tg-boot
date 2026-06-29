package pub.module.trade.api.messaging;

import pub.module.common.messaging.MqChannel;
import pub.module.common.messaging.MqMessageConsumer;
import pub.module.common.messaging.MqSubscribe;
import pub.module.trade.api.dto.TdOrderGoodsDTO;

/**
 * 订单商品支付成功 — MQ 全链路契约（生产 + 消费）。
 * <p>发布：{@code mqPublisher.publish(TradeOrderGoodsPaidConsumer.class, dto)}</p>
 */
@MqChannel(
        destination = TradeOrderGoodsPaidConsumer.DESTINATION,
        producerFunction = TradeOrderGoodsPaidConsumer.PRODUCER_FUNCTION
)
public interface TradeOrderGoodsPaidConsumer extends MqMessageConsumer<TdOrderGoodsDTO> {

    String DESTINATION = "trade.order-goods.paid";
    String PRODUCER_FUNCTION = "tradeOrderGoodsPaid";

    void onOrderGoodsPaid(TdOrderGoodsDTO message);

    @MqSubscribe(group = "dating", function = "datingTradeOrderGoodsPaid")
    interface Dating extends TradeOrderGoodsPaidConsumer {
    }

    @MqSubscribe(group = "distribution", function = "distTradeOrderGoodsPaid")
    interface Distribution extends TradeOrderGoodsPaidConsumer {
    }
}

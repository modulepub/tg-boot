package pub.module.trade.biz.messaging;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.common.messaging.MqPublisher;
import pub.module.trade.api.dto.TdOrderGoodsDTO;
import pub.module.trade.api.messaging.TradeOrderGoodsPaidConsumer;

/**
 * 订单商品支付成功后，通过消息队列通知下游业务域。
 */
@Slf4j
@Service
public class TradeOrderGoodsPaidPublisher {

    @Resource
    private MqPublisher mqPublisher;

    public void publishAfterCommit(TdOrderGoodsDTO orderGoods) {
        mqPublisher.publishAfterCommit(TradeOrderGoodsPaidConsumer.class, orderGoods);
        log.info("订单商品支付消息已登记 destination={} tdOdGdCode={} tdGdCgyCode={}",
                TradeOrderGoodsPaidConsumer.DESTINATION,
                orderGoods.getTdOdGdCode(), orderGoods.getTdGdCgyCode());
    }
}

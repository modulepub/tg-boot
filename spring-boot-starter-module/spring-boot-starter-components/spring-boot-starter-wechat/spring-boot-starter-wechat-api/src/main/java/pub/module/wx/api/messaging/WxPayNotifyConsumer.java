package pub.module.wx.api.messaging;

import pub.module.common.messaging.MqChannel;
import pub.module.common.messaging.MqMessageConsumer;
import pub.module.common.messaging.MqSubscribe;

/**
 * 微信支付结果通知 — MQ 全链路契约（生产 + 消费）。
 * <p>发布：{@code mqPublisher.publish(WxPayNotifyConsumer.class, message)}</p>
 */
@MqChannel(
        destination = WxPayNotifyConsumer.DESTINATION,
        producerFunction = WxPayNotifyConsumer.PRODUCER_FUNCTION
)
public interface WxPayNotifyConsumer extends MqMessageConsumer<WxPayNotifyMessage> {

    String DESTINATION = "wx.pay.notify";
    String PRODUCER_FUNCTION = "wxPayNotify";

    void onWxPayNotify(WxPayNotifyMessage message);

    @MqSubscribe(group = "trade", function = "tradeWxPayNotify")
    interface Trade extends WxPayNotifyConsumer {
    }
}

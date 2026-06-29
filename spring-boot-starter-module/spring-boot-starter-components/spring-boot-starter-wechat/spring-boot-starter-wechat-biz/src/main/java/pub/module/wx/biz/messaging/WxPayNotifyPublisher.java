package pub.module.wx.biz.messaging;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.common.messaging.MqPublisher;
import pub.module.wx.api.messaging.WxPayNotifyConsumer;
import pub.module.wx.api.messaging.WxPayNotifyMessage;

/**
 * 微信支付回调解析成功后，通过消息队列通知下游业务域。
 */
@Slf4j
@Service
public class WxPayNotifyPublisher {

    @Resource
    private MqPublisher mqPublisher;

    public void publishAfterCommit(WxPayNotifyMessage message) {
        mqPublisher.publishAfterCommit(WxPayNotifyConsumer.class, message);
        log.info("微信支付通知消息已发送 destination={} outTradeNo={} transactionId={}",
                WxPayNotifyConsumer.DESTINATION,
                message.getOutTradeNo(),
                message.getTransactionId());
    }
}

package pub.module.trade.biz.messaging;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.trade.biz.service.BizTradeOrderService;
import pub.module.wx.api.messaging.WxPayNotifyConsumer;
import pub.module.wx.api.messaging.WxPayNotifyMessage;

/**
 * 订阅微信支付结果通知，在交易域内标记订单已支付。
 */
@Slf4j
@Component
public class TradeWxPayNotifyHandler implements WxPayNotifyConsumer.Trade {

    @Resource
    private BizTradeOrderService bizTradeOrderService;

    @Override
    public void onWxPayNotify(WxPayNotifyMessage message) {
        if (message == null || message.getOutTradeNo() == null) {
            log.warn("微信支付通知消息无效，忽略");
            return;
        }
        if (!"SUCCESS".equals(message.getTradeState())) {
            log.info("微信支付通知非 SUCCESS，忽略 outTradeNo={} tradeState={}",
                    message.getOutTradeNo(), message.getTradeState());
            return;
        }
        try {
            bizTradeOrderService.paidOrder(message.getOutTradeNo());
            log.info("微信支付通知已处理 outTradeNo={} transactionId={}",
                    message.getOutTradeNo(), message.getTransactionId());
        }
        catch (Exception ex) {
            log.error("处理微信支付通知失败 outTradeNo={}", message.getOutTradeNo(), ex);
            throw ex;
        }
    }
}

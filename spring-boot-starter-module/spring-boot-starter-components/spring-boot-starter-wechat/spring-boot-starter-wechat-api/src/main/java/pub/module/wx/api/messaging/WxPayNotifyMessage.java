package pub.module.wx.api.messaging;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信支付结果通知消息体。
 */
@Data
public class WxPayNotifyMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 商户订单号（out_trade_no） */
    private String outTradeNo;

    /** 微信支付订单号 */
    private String transactionId;

    /** 交易状态，如 SUCCESS */
    private String tradeState;
}

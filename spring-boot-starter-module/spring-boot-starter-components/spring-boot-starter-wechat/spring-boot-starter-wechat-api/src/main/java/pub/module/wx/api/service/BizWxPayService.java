package pub.module.wx.api.service;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 微信支付业务服务接口
 * 定义微信支付相关的业务方法
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
public interface BizWxPayService {
    @Data
    class WxPayRepDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        @Schema(description = "用户号")
        private String openId;
        @Schema(description = "金额")
        private String amount;
        @Schema(description = "备注")
        private String remark;
        @Schema(description = "订单号")
        private String tradeNo;
        @Schema(description = "微信支付参数")
        private String packageValue;
        @Schema(description = "appId")
        private String appId;

    }

    @Data
    class JsapiResultDTO implements Serializable {
        private static final long serialVersionUID = 4465376277943307271L;
        private String appId;
        private String timeStamp;
        private String nonceStr;
        private String packageValue;
        private String signType;
        private String paySign;
    }

    JsapiResultDTO createOrderV3(WxPayRepDTO wxPayRepDTO);

    @Data
    class WxQueryPayReqDTO {
        String tradeNo;
        String transactionId;
    }

    WxPayOrderQueryV3Result queryOrderV3(WxQueryPayReqDTO wxQueryPayReqDTO);

    @Data
    class WxPayOrderQueryV3Result implements Serializable {
        String tradeNo;
        BigDecimal amount;
        Boolean paidSuccess;
    }
}

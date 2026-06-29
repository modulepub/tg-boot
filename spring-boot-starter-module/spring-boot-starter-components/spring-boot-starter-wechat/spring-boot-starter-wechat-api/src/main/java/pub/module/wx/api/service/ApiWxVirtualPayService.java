package pub.module.wx.api.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 微信小程序虚拟支付业务服务。
 */
public interface ApiWxVirtualPayService {

    @Data
    @Schema(description = "虚拟支付预下单请求")
    class VirtualPayReqDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "小程序 appId")
        private String appId;
        @Schema(description = "wx.login 返回的 code，用于换取 sessionKey")
        private String code;
        @Schema(description = "业务订单号（outTradeNo）")
        private String tradeNo;
        @Schema(description = "道具 ID（商品编码 tdGdCode）")
        private String productId;
        @Schema(description = "订单金额（元）")
        private BigDecimal amount;
        @Schema(description = "购买数量")
        private Integer buyQuantity;
        @Schema(description = "透传 attach")
        private String attach;
    }

    @Data
    @Schema(description = "虚拟支付调起参数")
    class VirtualPayResultDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String signData;
        private String paySig;
        private String signature;
        private String mode;
    }

    @Data
    @Schema(description = "虚拟支付查单请求")
    class VirtualPayQueryReqDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String appId;
        private String code;
        private String tradeNo;
    }

    @Data
    @Schema(description = "虚拟支付查单结果")
    class VirtualPayQueryResultDTO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String tradeNo;
        private Boolean paidSuccess;
    }

    VirtualPayResultDTO createPayment(VirtualPayReqDTO req);

    VirtualPayQueryResultDTO queryOrder(VirtualPayQueryReqDTO req);
}

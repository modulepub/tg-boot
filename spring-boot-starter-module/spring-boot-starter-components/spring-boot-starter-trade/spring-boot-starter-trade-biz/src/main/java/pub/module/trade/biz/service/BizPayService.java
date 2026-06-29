package pub.module.trade.biz.service;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.transaction.annotation.Transactional;
import pub.module.trade.crud.entity.TdOrder;

import java.math.BigDecimal;

/**
 * 支付
 */
public interface BizPayService {

    /** 支付渠道编码，见 {@link pub.module.trade.api.constants.TradePaidChannelEnum} */
    String paidChannelCode();

    @Data
    @Schema(description = "预支付DTO")
    class PrePayDTO {
        @Data
        @Schema(description = "预支付请求DTO")
        public static class Req{
            @Schema(description = "业务交易号（多个以逗号分隔，字符串长度不超过1000位）")
            public String tdOdCode;
            @Schema(description = "金额（业务系统需要校验金额，支付模块不对金额进行校验）")
            BigDecimal tdOdAmount;
            @Schema(description = "备注")
            String tdOdRemark;
            @Schema(description = "支付平台参数：前端根据各类支付平台传参，参数名称与平台提供的一致")
            private JSONObject platParam;
            @Schema(description = "支付密码")
            String password;
            /**支付渠道*/
            @Schema(description = "支付渠道")
            private java.lang.String tdPaidChannelCode;
        }

        @Data
        @Schema(description = "预支付请求DTO")
        public static class Res{
            @Schema(description = "支付平台参数：前端根据各类支付平台传参，参数名称与平台提供的一致")
            private JSONObject platParam;
        }


    }
    @Transactional(rollbackFor = Exception.class)
    PrePayDTO.Res prePay(PrePayDTO.Req prePayDTO);
    @Data
    @Schema(description = "预支付请求DTO")
    class PayResultReq {
        String tdOdCode;
        /**支付渠道*/
        @Schema(description = "支付渠道")
        private java.lang.String tdPaidChannelCode;
        @Schema(description = "支付平台参数：前端根据各类支付平台传参，参数名称与平台提供的一致")
        private JSONObject platParam;
    }

    TdOrder getPayResult(PayResultReq req);


}

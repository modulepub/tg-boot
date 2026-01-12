package pub.module.finance.api.service;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 支付
 */
public interface BizPayService  {
    @Data
    @Schema(description = "预支付DTO")
    class PrePayDTO {
        @Data
        @Schema(description = "预支付请求DTO")
        public static class Req{
            @Schema(description = "业务交易号（多个以逗号分隔，字符串长度不超过1000位）")
            public String tradeNo;
            @Schema(description = "金额（业务系统需要校验金额，支付模块不对金额进行校验）")
            BigDecimal amount;
            @Schema(description = "备注")
            String remark;
            @Schema(description = "账户编码（当使用微信支付宝直接支付时可留空，微信、支付宝自动扣款功能需要传入）")
            private String fcAcCode;
            @Schema(description = "支付平台参数：前端根据各类支付平台传参，参数名称与平台提供的一致")
            private JSONObject platParam;
            @Schema(description = "回调服务地址：支持http地址和api名称2种方式，回调参数为支付流水实体")
            public String notifyApi;
            @Schema(description = "期数（信用借贷使用）")
            public Integer period;
            @Schema(description = "支付密码")
            String password;
            @Schema(description = "支付的用户")
            String userCode;
            @Schema(description = "彼时支付用户的真实姓名")
            String userRealName;
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
    @Schema(description = "查询支付结果")
    class QueryPayResultDTO {
        @Data
        @Schema(description = "预支付请求DTO")
        public static class Req {
            String tradeNo;
            @Schema(description = "支付平台参数：前端根据各类支付平台传参，参数名称与平台提供的一致")
            private JSONObject platParam;
        }
        @Data
        @Schema(description = "预支付请求DTO")
        public static class Res {
            String tradeNo;
            Boolean paidSuccess;
        }
    }

    QueryPayResultDTO.Res getPayResult(QueryPayResultDTO.Req req);


}

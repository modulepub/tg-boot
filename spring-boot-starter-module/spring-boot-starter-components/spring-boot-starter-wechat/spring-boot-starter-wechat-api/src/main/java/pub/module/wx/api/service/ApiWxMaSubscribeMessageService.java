package pub.module.wx.api.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 微信小程序订阅消息发送（通用能力，不含业务模板定义）。
 */
public interface ApiWxMaSubscribeMessageService {

    @Data
    class SendRequest implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "小程序配置编码 wx_mini_config_code，优先于 appId")
        private String wxMiniConfigCode;

        @Schema(description = "小程序 appId，wxMiniConfigCode 为空时使用；均为空则取默认启用配置")
        private String appId;

        @Schema(description = "接收人 openId")
        private String toOpenId;

        @Schema(description = "订阅消息模板 ID")
        private String templateId;

        @Schema(description = "点击跳转小程序页面，不含开头 /")
        private String page;

        @Schema(description = "模板字段 key -> value，如 thing2、date1")
        private Map<String, String> data = new LinkedHashMap<>();

        @Schema(description = "幂等键，非空时同键仅发送一次")
        private String idempotentKey;
    }

    @Data
    class SendResult implements Serializable {
        private static final long serialVersionUID = 1L;

        private boolean success;
        private boolean skipped;
        private String wxErrCode;
        private String wxErrMsg;
    }

    SendResult send(SendRequest request);
}

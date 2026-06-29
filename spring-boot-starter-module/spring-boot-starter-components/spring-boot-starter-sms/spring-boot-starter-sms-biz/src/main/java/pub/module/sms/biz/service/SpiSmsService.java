package pub.module.sms.biz.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 短信渠道 SPI（模块内多实现，由 ApiSmsSendService 按 providerCode 路由）。
 */
public interface SpiSmsService {

    @Schema(description = "短信发送 SPI 入参")
    @Data
    @Builder
    class SpiSendSmsDTO {
        @Schema(description = "手机号（单发）")
        String mobile;
        @Schema(description = "手机号列表（群发）")
        List<String> mobileList;
        @Schema(description = "短信内容（纯文本渠道）")
        String content;
        @Schema(description = "模板 ID（模板渠道）")
        String templateId;
        @Schema(description = "模板参数")
        List<String> templateParams;
        @Schema(description = "短信渠道编码（见 SmsProviderCode 枚举）")
        String smsProviderCode;
    }

    /**
     * 短信渠道编码，见 {@link pub.module.sms.api.constants.SmsProviderCode}。
     */
    String providerCode();

    /**
     * @return 渠道返回的发送流水号（若渠道支持，否则可为 null）
     */
    String spiSendSms(SpiSendSmsDTO sendSmsDTO);
}

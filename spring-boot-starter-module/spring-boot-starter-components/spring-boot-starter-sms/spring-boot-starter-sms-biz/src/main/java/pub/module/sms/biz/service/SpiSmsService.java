package pub.module.sms.biz.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;


public interface SpiSmsService {

    @Schema(description = "发送短信")
    @Data
    @Builder
    class SpiSendSmsDTO {
        @Schema(description = "手机号")
        String mobile;
        @Schema(description = "短信内容")
        String content;
    }

    void spiSendSms(SpiSendSmsDTO sendSmsDTO);


}

package pub.module.sms.api.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;


public interface BizSmsService {

    @Schema(description = "发送短信")
    @Data
    @Builder
    class SendSmsDTO {
        @Schema(description = "手机号")
        String mobile;
        @Schema(description = "短信内容")
        String content;
        @Schema(description = "平台")
        String plateCode;
    }

    void sendSms(SendSmsDTO sendSmsDTO);


}

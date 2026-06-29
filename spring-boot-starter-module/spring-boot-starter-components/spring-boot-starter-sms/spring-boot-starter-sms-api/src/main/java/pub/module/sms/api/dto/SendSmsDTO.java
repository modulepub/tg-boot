package pub.module.sms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 短信发送请求（供其他模块跨模块调用，按 providerCode 路由 SPI 实现）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "短信发送请求")
public class SendSmsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "手机号（单发时使用，与 mobileList 二选一）")
    private String mobile;

    @Schema(description = "手机号列表（群发时使用，与 mobile 二选一）")
    private List<String> mobileList;

    @Schema(description = "系统短信模板编码（必须先在 sms_template 表中配置，渠道方模板 ID 会从表中自动查询得到）")
    private String smsTemplateCode;

    @Schema(description = "模板参数，按模板变量顺序传入")
    private List<String> templateParams;

    @Schema(description = "短信渠道编码，见 SmsProviderCode 枚举（默认 tencent）")
    private String smsProviderCode;
}

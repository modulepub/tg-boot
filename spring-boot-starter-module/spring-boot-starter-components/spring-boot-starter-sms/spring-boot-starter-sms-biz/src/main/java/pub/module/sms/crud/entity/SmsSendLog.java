package pub.module.sms.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * 短信发送记录，表 sms_send_log。
 */
@Data
@TableName("sms_send_log")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "短信发送记录")
public class SmsSendLog extends BaseEntity {

    @Schema(description = "短信发送记录业务编码")
    private String smsSendLogCode;

    @Schema(description = "短信渠道编码，见 SmsProviderCode 枚举")
    private String smsProviderCode;

    @Schema(description = "短信模板编码（关联 sms_template）")
    private String smsTemplateCode;

    @Schema(description = "手机号（多个用逗号分隔）")
    private String mobile;

    @Schema(description = "模板参数（JSON 数组字符串）")
    private String templateParams;

    @Schema(description = "是否成功：1-成功 0-失败")
    private String successCode;

    @Schema(description = "渠道方原始请求参数（JSON）")
    private String providerRequestJson;

    @Schema(description = "渠道方原始返回参数（JSON）")
    private String providerResponseJson;

    @Schema(description = "错误信息（失败时记录）")
    private String errorMessage;
}

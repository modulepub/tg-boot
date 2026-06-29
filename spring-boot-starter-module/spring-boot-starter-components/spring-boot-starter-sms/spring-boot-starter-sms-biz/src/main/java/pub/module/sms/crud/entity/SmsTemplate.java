package pub.module.sms.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * 短信模板配置，表 sms_template。
 */
@Data
@TableName("sms_template")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "短信模板配置")
public class SmsTemplate extends BaseEntity {

    @Schema(description = "短信模板业务编码（主键语义，表字段 sms_template_code）")
    private String smsTemplateCode;

    @Schema(description = "短信渠道编码，见 SmsProviderCode 枚举")
    private String smsProviderCode;

    @Schema(description = "渠道方模板 ID（腾讯云等渠道返回的模板 ID）")
    private String smsTemplateId;

    @Schema(description = "模板内容（用于预览/说明）")
    private String smsTemplateContent;

    @Schema(description = "启用状态编码：1-是 0-否")
    private String smsTemplateEnabledCode;

    @Schema(description = "备注")
    private String smsTemplateRemark;
}

package pub.module.sms.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * 腾讯云短信配置，表 sms_tencent_config。
 */
@Data
@TableName("sms_tencent_config")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "腾讯云短信配置")
public class SmsTencentConfig extends BaseEntity {

    @Schema(description = "腾讯云短信配置业务编码")
    private String smsTencentConfigCode;

    @Schema(description = "腾讯云 SecretId")
    private String smsTencentConfigSecretId;

    @Schema(description = "腾讯云 SecretKey")
    private String smsTencentConfigSecretKey;

    @Schema(description = "短信 SdkAppId")
    private String smsTencentConfigSdkAppId;

    @Schema(description = "默认短信签名")
    private String smsTencentConfigSignName;

    @Schema(description = "接入地域，默认 ap-guangzhou")
    private String smsTencentConfigRegion;

    @Schema(description = "启用状态编码：1-是 0-否")
    private String smsTencentConfigEnabledCode;

    @Schema(description = "备注")
    private String smsTencentConfigRemark;
}

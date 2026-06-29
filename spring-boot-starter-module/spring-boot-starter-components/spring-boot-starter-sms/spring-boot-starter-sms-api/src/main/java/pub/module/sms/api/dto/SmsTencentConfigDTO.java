package pub.module.sms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 腾讯云短信配置 DTO（管理端维护 sms_tencent_config）。
 */
@Data
@Schema(description = "腾讯云短信配置 DTO")
public class SmsTencentConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "腾讯云短信配置编码（主键）")
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

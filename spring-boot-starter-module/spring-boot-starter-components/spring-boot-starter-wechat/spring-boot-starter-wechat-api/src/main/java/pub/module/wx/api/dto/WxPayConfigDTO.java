package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信支付配置 DTO（管理端维护）。
 */
@Data
@Schema(description = "微信支付配置 DTO")
public class WxPayConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "微信支付配置编码（主键）")
    private String wxPayConfigCode;

    @Schema(description = "微信 AppId")
    private String wxPayConfigAppId;

    @Schema(description = "微信商户号")
    private String wxPayConfigMchId;

    @Schema(description = "APIv3 密钥")
    private String wxPayConfigApiV3Key;

    @Schema(description = "支付结果通知 URL")
    private String wxPayConfigNotifyUrl;

    @Schema(description = "商户 API 私钥 PEM 字符串")
    private String wxPayConfigPrivateKey;

    @Schema(description = "商户 API 证书 PEM 字符串")
    private String wxPayConfigPrivateCert;

    @Schema(description = "是否沙箱：0-否，1-是")
    private Integer wxPayConfigUseSandbox;

    @Schema(description = "启用状态编码")
    private String wxPayConfigEnabledCode;

    @Schema(description = "备注")
    private String wxPayConfigRemark;
}

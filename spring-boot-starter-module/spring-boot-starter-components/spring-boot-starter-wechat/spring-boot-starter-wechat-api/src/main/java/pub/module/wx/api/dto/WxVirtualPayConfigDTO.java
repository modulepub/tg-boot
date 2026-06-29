package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信小程序虚拟支付配置 DTO（管理端维护）。
 */
@Data
@Schema(description = "微信小程序虚拟支付配置 DTO")
public class WxVirtualPayConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "虚拟支付配置编码（主键）")
    private String wxVirtualPayConfigCode;

    @Schema(description = "小程序 AppId")
    private String wxVirtualPayConfigAppId;

    @Schema(description = "米大师 OfferId")
    private String wxVirtualPayConfigOfferId;

    @Schema(description = "沙箱 AppKey")
    private String wxVirtualPayConfigAppKeySandbox;

    @Schema(description = "现网 AppKey")
    private String wxVirtualPayConfigAppKeyProd;

    @Schema(description = "是否沙箱：0-否，1-是")
    private Integer wxVirtualPayConfigUseSandbox;

    @Schema(description = "发货/支付通知 URL")
    private String wxVirtualPayConfigNotifyUrl;

    @Schema(description = "启用状态编码")
    private String wxVirtualPayConfigEnabledCode;

    @Schema(description = "备注")
    private String wxVirtualPayConfigRemark;
}

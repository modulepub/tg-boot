package pub.module.wx.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

/**
 * 微信支付配置，表 wx_pay_config。
 */
@Data
@TableName("wx_pay_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title = "wx_pay_config", description = "微信支付配置")
public class WxPayConfig extends BaseEntity {

    @Schema(description = "微信支付配置编码（业务主键）")
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

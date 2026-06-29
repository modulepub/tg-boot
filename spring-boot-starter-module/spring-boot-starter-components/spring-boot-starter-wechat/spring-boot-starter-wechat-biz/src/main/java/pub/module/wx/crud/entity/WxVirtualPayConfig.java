package pub.module.wx.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

/**
 * 微信小程序虚拟支付配置，表 wx_virtual_pay_config。
 */
@Data
@TableName("wx_virtual_pay_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title = "wx_virtual_pay_config", description = "微信小程序虚拟支付配置")
public class WxVirtualPayConfig extends BaseEntity {

    @Schema(description = "虚拟支付配置编码（业务主键）")
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

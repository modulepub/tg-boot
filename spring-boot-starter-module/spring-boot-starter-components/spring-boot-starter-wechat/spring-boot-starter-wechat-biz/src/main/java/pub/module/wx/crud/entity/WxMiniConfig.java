package pub.module.wx.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

/**
 * 微信小程序配置，表 wx_mini_config。
 */
@Data
@TableName("wx_mini_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title = "wx_mini_config", description = "微信小程序配置")
public class WxMiniConfig extends BaseEntity {

    @Schema(description = "配置编码（业务主键）")
    private String wxMiniConfigCode;

    @Schema(description = "配置名称")
    private String wxMiniConfigName;

    @Schema(description = "小程序 AppId")
    private String wxMiniConfigAppId;

    @Schema(description = "小程序 AppSecret")
    private String wxMiniConfigAppSecret;

    @Schema(description = "消息格式 JSON/XML")
    private String wxMiniConfigMsgDataFormat;

    @Schema(description = "消息推送 Token（URL 验签用）")
    private String wxMiniConfigToken;

    @Schema(description = "消息推送 EncodingAESKey（安全模式 AES 解密用，明文模式可空）")
    private String wxMiniConfigAesKey;

    @Schema(description = "启用状态编码：1-是 0-否")
    private String wxMiniConfigEnabledCode;

    @Schema(description = "备注")
    private String wxMiniConfigRemark;
}

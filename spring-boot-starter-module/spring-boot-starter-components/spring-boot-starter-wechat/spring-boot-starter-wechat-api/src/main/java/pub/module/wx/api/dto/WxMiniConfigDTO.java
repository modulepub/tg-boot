package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信小程序配置 DTO（管理端维护）。
 */
@Data
@Schema(description = "微信小程序配置 DTO")
public class WxMiniConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "配置编码（主键）")
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

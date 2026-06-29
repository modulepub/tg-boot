package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信公众号配置 DTO（管理端维护）。
 */
@Data
@Schema(description = "微信公众号配置 DTO")
public class WxMpConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "配置编码（主键）")
    private String wxMpConfigCode;

    @Schema(description = "配置名称")
    private String wxMpConfigName;

    @Schema(description = "公众号 AppId")
    private String wxMpConfigAppId;

    @Schema(description = "公众号 AppSecret")
    private String wxMpConfigAppSecret;

    @Schema(description = "消息校验 Token")
    private String wxMpConfigToken;

    @Schema(description = "消息加解密密钥 EncodingAESKey")
    private String wxMpConfigAesKey;

    @Schema(description = "启用状态：0-否 1-是")
    private String wxMpConfigEnabledStatusCode;

    @Schema(description = "自定义菜单 JSON")
    private String wxMpConfigMenuJson;

    @Schema(description = "接管回复的 AI 智能体编码")
    private String wxMpConfigAiAgentCode;

    @Schema(description = "AI 自动回复：0-否 1-是")
    private String wxMpConfigAiAutoReplyStatusCode;

    @Schema(description = "关注回复：0-否 1-是")
    private String wxMpConfigSubscribeReplyStatusCode;

    @Schema(description = "关注回复图文 JSON（title/description/picUrl/url）")
    private String wxMpConfigSubscribeReplyJson;

    @Schema(description = "备注")
    private String wxMpConfigRemark;
}

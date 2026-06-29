package pub.module.wx.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import pub.module.common.model.po.BaseEntity;
import pub.module.common.enums.StatusCodeEnum;

import java.time.LocalDateTime;

/**
 * 微信公众号配置，表 wx_mp_config。
 */
@Data
@TableName("wx_mp_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title = "wx_mp_config", description = "微信公众号配置")
public class WxMpConfig extends BaseEntity {

    @Schema(description = "配置编码（业务主键）")
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
    private StatusCodeEnum wxMpConfigEnabledStatusCode;

    @Schema(description = "自定义菜单 JSON")
    private String wxMpConfigMenuJson;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "菜单最近发布时间")
    private LocalDateTime wxMpConfigMenuPublishedTime;

    @Schema(description = "接管回复的 AI 智能体编码")
    private String wxMpConfigAiAgentCode;

    @Schema(description = "AI 自动回复：0-否 1-是")
    private StatusCodeEnum wxMpConfigAiAutoReplyStatusCode;

    @Schema(description = "关注回复：0-否 1-是")
    private StatusCodeEnum wxMpConfigSubscribeReplyStatusCode;

    @Schema(description = "关注回复图文 JSON（title/description/picUrl/url）")
    private String wxMpConfigSubscribeReplyJson;

    @Schema(description = "备注")
    private String wxMpConfigRemark;
}

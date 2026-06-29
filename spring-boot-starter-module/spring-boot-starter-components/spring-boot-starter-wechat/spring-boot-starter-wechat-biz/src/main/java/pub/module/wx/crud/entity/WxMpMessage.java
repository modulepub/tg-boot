package pub.module.wx.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

/**
 * 微信公众号消息，表 wx_mp_message。
 */
@Data
@TableName("wx_mp_message")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title = "wx_mp_message", description = "微信公众号消息")
public class WxMpMessage extends BaseEntity {

    @Schema(description = "消息业务编码")
    private String wxMpMessageCode;

    @Schema(description = "公众号配置编码")
    private String wxMpConfigCode;

    @Schema(description = "粉丝 OpenId")
    private String wxMpFanOpenId;

    @Schema(description = "方向：in/out")
    private String wxMpMessageDirectionCode;

    @Schema(description = "消息类型")
    private String wxMpMessageTypeCode;

    @Schema(description = "文本内容或摘要")
    private String wxMpMessageContent;

    @Schema(description = "媒体 ID")
    private String wxMpMessageMediaId;

    @Schema(description = "微信消息 ID")
    private String wxMpMessageWxMsgId;

    @Schema(description = "出站来源：auto_ai/manual/system")
    private String wxMpMessageReplySourceCode;

    @Schema(description = "关联 AI 会话编码")
    private String aiChatSessionCode;
}

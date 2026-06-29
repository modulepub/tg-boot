package pub.module.im.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * IM-消息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_message")
@Schema(description = "IM-消息")
public class ImMessage extends BaseEntity {

    @Schema(description = "业务编码")
    private String imMessageCode;

    @Schema(description = "会话编码")
    private String imMessageConversationCode;

    @Schema(description = "发送方用户编码")
    private String imMessageFromUserCode;

    @Schema(description = "接收方用户编码")
    private String imMessageToUserCode;

    @Schema(description = "消息类型 text=文本 rich=图文")
    private String imMessageTypeCode;

    @Schema(description = "文本内容")
    private String imMessageContent;

    @Schema(description = "图文标题")
    private String imMessageTitle;

    @Schema(description = "图文图片地址")
    private String imMessageImageUrl;

    @Schema(description = "图文跳转链接")
    private String imMessageLinkUrl;

    @Schema(description = "已读状态 0=未读 1=已读")
    private String imMessageReadStatusCode;

    @Schema(description = "发送状态 0=失败 1=成功")
    private String imMessageSendStatusCode;
}

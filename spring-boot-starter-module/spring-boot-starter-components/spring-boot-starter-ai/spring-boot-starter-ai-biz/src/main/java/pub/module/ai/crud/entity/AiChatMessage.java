package pub.module.ai.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * AI 对话消息，表 ai_chat_message。
 */
@Data
@TableName("ai_chat_message")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "AI 对话消息")
public class AiChatMessage extends BaseEntity {

    @Schema(description = "消息业务编码")
    private String aiChatMessageCode;

    @Schema(description = "会话业务编码")
    private String aiChatSessionCode;

    @Schema(description = "消息角色：system/user/assistant")
    private String aiChatMessageRoleCode;

    @Schema(description = "消息内容")
    private String aiChatMessageContent;

    @Schema(description = "消息排序号")
    private Integer aiChatMessageSortNo;
}

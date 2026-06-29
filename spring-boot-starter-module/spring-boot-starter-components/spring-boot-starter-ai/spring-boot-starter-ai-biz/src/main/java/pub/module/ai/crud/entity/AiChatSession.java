package pub.module.ai.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * AI 对话会话，表 ai_chat_session。
 */
@Data
@TableName("ai_chat_session")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "AI 对话会话")
public class AiChatSession extends BaseEntity {

    @Schema(description = "会话业务编码")
    private String aiChatSessionCode;

    @Schema(description = "用户编码")
    private String userCode;

    @Schema(description = "智能体业务编码")
    private String aiAgentCode;

    @Schema(description = "会话标题")
    private String aiChatSessionTitle;
}

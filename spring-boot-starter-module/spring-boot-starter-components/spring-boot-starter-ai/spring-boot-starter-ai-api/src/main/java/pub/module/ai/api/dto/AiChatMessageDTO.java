package pub.module.ai.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "AI 对话消息")
public class AiChatMessageDTO {

    @Schema(description = "消息角色：system/user/assistant")
    private String aiChatMessageRoleCode;

    @Schema(description = "消息内容")
    private String aiChatMessageContent;
}

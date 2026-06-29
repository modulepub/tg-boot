package pub.module.ai.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "AI 对话请求")
public class AiChatRequestDTO {

    @Schema(description = "智能体业务编码")
    private String aiAgentCode;

    @Schema(description = "用户编码")
    private String userCode;

    @Schema(description = "会话业务编码（为空则新建会话）")
    private String aiChatSessionCode;

    @Schema(description = "用户输入消息")
    private String message;
}

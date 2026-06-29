package pub.module.ai.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "AI 对话响应")
public class AiChatResponseDTO {

    @Schema(description = "会话业务编码")
    private String aiChatSessionCode;

    @Schema(description = "助手回复内容")
    private String reply;

    @Schema(description = "使用的模型")
    private String model;

    @Schema(description = "提示词 token 数")
    private Integer promptTokens;

    @Schema(description = "回复 token 数")
    private Integer completionTokens;

    @Schema(description = "总 token 数")
    private Integer totalTokens;

    @Schema(description = "本次消耗总价")
    private BigDecimal totalPrice;

    @Schema(description = "消耗明细业务编码")
    private String aiUsageRecordCode;

    @Schema(description = "完整对话消息列表")
    private List<AiChatMessageDTO> messages;
}

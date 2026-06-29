package pub.module.ai.api.service;

import pub.module.ai.api.dto.AiChatRequestDTO;
import pub.module.ai.api.dto.AiChatResponseDTO;

/**
 * AI 对话跨模块 API。
 */
public interface ApiAiChatService {

    /**
     * 与智能体对话（记录消息与消耗明细）。
     */
    AiChatResponseDTO chat(AiChatRequestDTO request);
}

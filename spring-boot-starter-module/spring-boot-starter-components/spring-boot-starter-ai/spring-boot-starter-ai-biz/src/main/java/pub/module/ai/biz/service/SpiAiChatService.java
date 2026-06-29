package pub.module.ai.biz.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * AI 对话渠道 SPI（OpenAI 兼容协议，模块内多实现由注册表路由）。
 */
public interface SpiAiChatService {

    @Data
    @Builder
    @Schema(description = "AI 对话 SPI 入参")
    class SpiChatDTO {
        @Schema(description = "API Base URL")
        private String baseUrl;
        @Schema(description = "API Key")
        private String apiKey;
        @Schema(description = "模型名称")
        private String model;
        @Schema(description = "对话消息列表")
        private List<SpiChatMessage> messages;
    }

    @Data
    @Builder
    @Schema(description = "SPI 对话消息")
    class SpiChatMessage {
        private String role;
        private String content;
    }

    @Data
    @Builder
    @Schema(description = "AI 对话 SPI 出参")
    class SpiChatResult {
        private String reply;
        private String model;
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
        private String requestJson;
        private String responseJson;
    }

    /**
     * AI 提供商编码，见 {@link pub.module.ai.api.constants.AiProviderCode}。
     */
    String providerCode();

    SpiChatResult spiChat(SpiChatDTO chatDTO);
}

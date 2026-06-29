package pub.module.ai.api.constants;

import lombok.Getter;

/**
 * AI 接口提供商编码（OpenAI 兼容协议）。
 */
@Getter
public enum AiProviderCode {

    OPENAI("openai", "OpenAI"),
    AZURE_OPENAI("azureOpenai", "Azure OpenAI"),
    DEEPSEEK("deepseek", "DeepSeek"),
    MOONSHOT("moonshot", "Moonshot"),
    ZHIPU("zhipu", "智谱 AI"),
    CUSTOM("custom", "自定义 OpenAI 兼容"),
    ;

    private final String code;
    private final String desc;

    AiProviderCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

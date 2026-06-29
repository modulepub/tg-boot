package pub.module.ai.biz.exception;

import lombok.Getter;

/**
 * AI 对话调用异常。
 */
@Getter
public class AiChatException extends RuntimeException {

    private final String requestJson;
    private final String responseJson;

    public AiChatException(String message) {
        super(message);
        this.requestJson = null;
        this.responseJson = null;
    }

    public AiChatException(String message, String requestJson, String responseJson) {
        super(message);
        this.requestJson = requestJson;
        this.responseJson = responseJson;
    }
}

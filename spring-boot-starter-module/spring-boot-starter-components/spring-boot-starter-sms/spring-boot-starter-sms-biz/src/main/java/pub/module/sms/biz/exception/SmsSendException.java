package pub.module.sms.biz.exception;

import lombok.Getter;

/**
 * 短信渠道发送失败，携带请求/响应快照供发送日志记录。
 */
@Getter
public class SmsSendException extends IllegalStateException {

    private final String requestJson;
    private final String responseJson;

    public SmsSendException(String message, String requestJson, String responseJson) {
        super(message);
        this.requestJson = requestJson;
        this.responseJson = responseJson;
    }

    public SmsSendException(String message, String requestJson, String responseJson, Throwable cause) {
        super(message, cause);
        this.requestJson = requestJson;
        this.responseJson = responseJson;
    }
}

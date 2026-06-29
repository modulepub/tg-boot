package pub.module.ai.api.constants;

import lombok.Getter;

/**
 * 对话消息角色编码（对齐 OpenAI Chat Completions API）。
 */
@Getter
public enum AiChatMessageRoleCode {

    SYSTEM("system", "系统"),
    USER("user", "用户"),
    ASSISTANT("assistant", "助手"),
    ;

    private final String code;
    private final String desc;

    AiChatMessageRoleCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

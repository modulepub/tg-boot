package pub.module.wx.api.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 公众号出站消息来源。
 */
@Getter
@RequiredArgsConstructor
public enum WxMpMessageReplySourceCode {

    AUTO_AI("auto_ai", "AI 自动回复"),
    MANUAL("manual", "人工回复"),
    SUBSCRIBE_REPLY("subscribe_reply", "关注回复"),
    SYSTEM("system", "系统消息");

    private final String code;
    private final String label;
}

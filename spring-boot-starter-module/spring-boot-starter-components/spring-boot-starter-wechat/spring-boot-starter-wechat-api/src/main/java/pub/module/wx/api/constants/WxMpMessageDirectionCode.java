package pub.module.wx.api.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 公众号消息方向。
 */
@Getter
@RequiredArgsConstructor
public enum WxMpMessageDirectionCode {

    IN("in", "用户发来"),
    OUT("out", "公众号发出");

    private final String code;
    private final String label;
}

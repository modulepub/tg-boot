package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcAcLogNotifyStatusCodeEnum {
    OK("1", "成功"),
    FAIL("0", "失败"),
    ;
    private final String code;
    private final String text;

    FcAcLogNotifyStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

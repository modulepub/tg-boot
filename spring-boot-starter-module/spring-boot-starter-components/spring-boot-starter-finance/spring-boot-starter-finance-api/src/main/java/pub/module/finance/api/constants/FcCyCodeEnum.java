package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcCyCodeEnum {
    CNY("CNY", "人民币"),
    ;
    private final String code;
    private final String text;

    FcCyCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

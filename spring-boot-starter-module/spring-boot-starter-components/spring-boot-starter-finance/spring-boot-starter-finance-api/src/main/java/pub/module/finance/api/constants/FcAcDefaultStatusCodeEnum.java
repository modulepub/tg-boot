package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcAcDefaultStatusCodeEnum {
    NOT_PAY("0", "否"),
    PAID("1", "是"),
    ;
    private final String code;
    private final String text;

    FcAcDefaultStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

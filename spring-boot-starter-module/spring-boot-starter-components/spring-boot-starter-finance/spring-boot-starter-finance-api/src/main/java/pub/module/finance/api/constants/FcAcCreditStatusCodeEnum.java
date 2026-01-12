package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcAcCreditStatusCodeEnum {
    NOT("-1", "未授信"),
    ING("0", "审核中"),
    PASSED("1", "已通过"),
    REJECT("2", "未通过"),
            ;
    private final String code;
    private final String text;

    FcAcCreditStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

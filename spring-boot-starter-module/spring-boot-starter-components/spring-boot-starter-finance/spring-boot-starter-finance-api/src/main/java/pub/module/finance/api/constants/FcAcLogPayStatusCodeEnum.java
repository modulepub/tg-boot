package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcAcLogPayStatusCodeEnum {

    NOT_PAY("0", "未支付"),
    PAID("1", "已支付"),
    FAIL("2", "支付失败"),
    ;
    private final String code;
    private final String text;

    FcAcLogPayStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

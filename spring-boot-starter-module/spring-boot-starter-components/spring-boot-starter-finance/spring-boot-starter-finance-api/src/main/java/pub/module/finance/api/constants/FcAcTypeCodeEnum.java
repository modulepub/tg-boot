package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcAcTypeCodeEnum {
    BANK("1", "银行卡"),
    ALIPAY("2", "支付宝"),
    WECHAT("3", "微信"),
    CREDIT("4", "信用账户"),
    ;
    private final String code;
    private final String text;

    FcAcTypeCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

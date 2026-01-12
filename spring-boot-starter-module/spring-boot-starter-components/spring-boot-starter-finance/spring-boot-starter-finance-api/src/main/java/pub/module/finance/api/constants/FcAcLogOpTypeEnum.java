package pub.module.finance.api.constants;


import lombok.Getter;

@Getter
public enum FcAcLogOpTypeEnum {
    PAY("1", "支付"),
    RECHARGE("2", "充值"),
    ;
    private final String code;
    private final String text;

    FcAcLogOpTypeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

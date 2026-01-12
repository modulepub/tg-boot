package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcAcPayStatusCodeEnum {
    ING("0", "支付中/放款中"),
    NORMAL("1", "正常"),
    ;
    private final String code;
    private final String text;

    FcAcPayStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

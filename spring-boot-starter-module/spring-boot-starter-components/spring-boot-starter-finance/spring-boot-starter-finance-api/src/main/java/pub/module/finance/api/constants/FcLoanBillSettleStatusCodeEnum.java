package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcLoanBillSettleStatusCodeEnum {
    NOT("0", "未结清"),
    YES("1", "已结清"),
    ;
    private final String code;
    private final String text;

    FcLoanBillSettleStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

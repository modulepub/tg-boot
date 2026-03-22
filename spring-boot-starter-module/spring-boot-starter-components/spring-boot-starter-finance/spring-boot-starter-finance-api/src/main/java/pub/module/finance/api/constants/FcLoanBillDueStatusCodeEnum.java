package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcLoanBillDueStatusCodeEnum {
    NOT("0", "未到期"),
    YES("1", "已到期"),
            ;
    private final String code;
    private final String text;

    FcLoanBillDueStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcLoanBillOverdueStatusCodeEnum {
    NOT("0", "未逾期"),
    YES("1", "已逾期"),
    ;
    private final String code;
    private final String text;

    FcLoanBillOverdueStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

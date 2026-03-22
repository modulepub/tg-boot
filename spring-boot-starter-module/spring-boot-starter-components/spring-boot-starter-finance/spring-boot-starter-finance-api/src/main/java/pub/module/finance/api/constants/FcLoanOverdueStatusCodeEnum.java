package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcLoanOverdueStatusCodeEnum {
    NOT("0", "未逾期"),
    YES("1", "已逾期"),
    ;
    private final String code;
    private final String text;

    FcLoanOverdueStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

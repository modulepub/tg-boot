package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcLoanReceivedStatusCodeEnum {
    YES("1", "已放款"),
    NOT("2", "未放款"),
    ;
    private final String code;
    private final String text;

    FcLoanReceivedStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

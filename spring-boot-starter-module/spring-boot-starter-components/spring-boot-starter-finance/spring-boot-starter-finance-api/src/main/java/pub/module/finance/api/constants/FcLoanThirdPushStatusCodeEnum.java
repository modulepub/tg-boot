package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcLoanThirdPushStatusCodeEnum {
    NOT("0", "否"),
    YES("1", "是"),
    ;
    private final String code;
    private final String text;

    FcLoanThirdPushStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

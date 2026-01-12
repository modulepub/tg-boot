package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcLoanApprovalStatusCodeEnum {
    ING("0", "审核中"),
    PASS("1", "pass"),
    REJECT("2", "reject"),
    ;
    private final String code;
    private final String text;

    FcLoanApprovalStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

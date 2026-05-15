package pub.module.dating.api.constants;

import lombok.Getter;

@Getter
public enum DtComApprovalStatusCodeEnum {
    ING("1", "审核中"),
    SUCCESS ("2", "审核通过"),
    FAIL ("3", "审核失败"),
    ;
    private final String code;
    private final String desc;

    DtComApprovalStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

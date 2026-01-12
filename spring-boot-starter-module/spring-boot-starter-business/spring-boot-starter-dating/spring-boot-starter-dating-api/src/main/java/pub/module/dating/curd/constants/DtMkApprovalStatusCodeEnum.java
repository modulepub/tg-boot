package pub.module.dating.curd.constants;

import lombok.Getter;

@Getter
public enum DtMkApprovalStatusCodeEnum {
    NOT("0", "未提交"),
    ING("1", "审核中"),
    SUCCESS ("2", "审核通过"),
    FAIL ("3", "审核失败"),
    ;
    private final String code;
    private final String desc;

    DtMkApprovalStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

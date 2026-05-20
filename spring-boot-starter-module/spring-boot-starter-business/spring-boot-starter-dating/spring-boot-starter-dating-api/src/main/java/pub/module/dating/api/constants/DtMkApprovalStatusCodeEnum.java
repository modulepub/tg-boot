package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DtMkApprovalStatusCodeEnum implements BaseEnum {
    NOT("0", "未提交"),
    ING("1", "审核中"),
    SUCCESS("2", "审核通过"),
    FAIL("3", "审核失败"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DtMkApprovalStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DtMkApprovalStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DtMkApprovalStatusCodeEnum.class);
    }
}

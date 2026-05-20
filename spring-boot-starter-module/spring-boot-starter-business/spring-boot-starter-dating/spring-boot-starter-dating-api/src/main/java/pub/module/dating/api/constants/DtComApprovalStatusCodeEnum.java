package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DtComApprovalStatusCodeEnum implements BaseEnum {
    ING("1", "审核中"),
    SUCCESS("2", "审核通过"),
    FAIL("3", "审核失败"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DtComApprovalStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DtComApprovalStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DtComApprovalStatusCodeEnum.class);
    }
}

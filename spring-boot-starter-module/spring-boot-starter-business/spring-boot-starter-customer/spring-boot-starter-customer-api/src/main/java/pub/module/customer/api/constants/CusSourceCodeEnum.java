package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusSourceCode */
@Getter
public enum CusSourceCodeEnum implements BaseEnum {
    IMPORT("1", "导入"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusSourceCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusSourceCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusSourceCodeEnum.class);
    }

    @Deprecated
    public static CusSourceCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusSourceCodeEnum.class);
    }
}

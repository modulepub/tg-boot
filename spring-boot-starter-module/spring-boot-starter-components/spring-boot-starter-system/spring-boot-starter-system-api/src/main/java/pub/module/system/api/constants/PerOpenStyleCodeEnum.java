package pub.module.system.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = perOpenStyleCode */
@Getter
public enum PerOpenStyleCodeEnum implements BaseEnum {
    INTERNAL("0", "内部打开"),
    EXTERNAL("1", "外部打开"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    PerOpenStyleCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static PerOpenStyleCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, PerOpenStyleCodeEnum.class);
    }
}

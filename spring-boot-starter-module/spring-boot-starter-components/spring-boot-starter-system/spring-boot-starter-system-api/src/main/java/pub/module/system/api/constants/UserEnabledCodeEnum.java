package pub.module.system.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * 字典 dict_code = userEnabledCode。
 */
@Getter
public enum UserEnabledCodeEnum implements BaseEnum {
    DISABLED("0", "未启用"),
    ENABLED("1", "已启用"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    UserEnabledCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static UserEnabledCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, UserEnabledCodeEnum.class);
    }

    @Deprecated
    public static UserEnabledCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, UserEnabledCodeEnum.class);
    }
}

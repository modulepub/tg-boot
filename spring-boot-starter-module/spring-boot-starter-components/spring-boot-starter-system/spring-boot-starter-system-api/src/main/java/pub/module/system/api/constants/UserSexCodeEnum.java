package pub.module.system.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * 字典 dict_code = userSexCode。
 */
@Getter
public enum UserSexCodeEnum implements BaseEnum {
    MAN("1", "男"),
    WOMAN("2", "女"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    UserSexCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static UserSexCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, UserSexCodeEnum.class);
    }

    /** @deprecated 请使用 {@link BaseEnum#parse(Object, Class)} / {@link #fromJson(Object)} */
    @Deprecated
    public static UserSexCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, UserSexCodeEnum.class);
    }
}

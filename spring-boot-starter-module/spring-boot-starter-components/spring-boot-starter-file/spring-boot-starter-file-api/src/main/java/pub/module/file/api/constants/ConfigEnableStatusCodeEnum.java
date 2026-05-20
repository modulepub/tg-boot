package pub.module.file.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = configEnableStatusCode */
@Getter
public enum ConfigEnableStatusCodeEnum implements BaseEnum {
    DISABLED("0", "禁用"),
    ENABLED("1", "启用"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    ConfigEnableStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static ConfigEnableStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, ConfigEnableStatusCodeEnum.class);
    }

    @Deprecated
    public static ConfigEnableStatusCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, ConfigEnableStatusCodeEnum.class);
    }
}

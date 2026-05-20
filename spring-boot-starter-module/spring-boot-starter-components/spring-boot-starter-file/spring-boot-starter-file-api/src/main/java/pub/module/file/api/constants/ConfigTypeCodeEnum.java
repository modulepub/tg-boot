package pub.module.file.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = configTypeCode */
@Getter
public enum ConfigTypeCodeEnum implements BaseEnum {
    FILE_CONFIG("fileConfig", "文件系统"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    ConfigTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static ConfigTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, ConfigTypeCodeEnum.class);
    }

    @Deprecated
    public static ConfigTypeCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, ConfigTypeCodeEnum.class);
    }
}

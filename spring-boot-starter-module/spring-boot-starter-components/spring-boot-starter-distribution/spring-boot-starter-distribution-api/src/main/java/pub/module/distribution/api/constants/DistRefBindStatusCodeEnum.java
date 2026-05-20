package pub.module.distribution.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DistRefBindStatusCodeEnum implements BaseEnum {
    VALID("valid", "有效"),
    INVALID("invalid", "无效"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DistRefBindStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DistRefBindStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DistRefBindStatusCodeEnum.class);
    }
}

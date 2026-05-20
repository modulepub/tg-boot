package pub.module.distribution.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DistRuleEnabledCodeEnum implements BaseEnum {
    ENABLED("1", "启用"),
    DISABLED("0", "停用"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DistRuleEnabledCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DistRuleEnabledCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DistRuleEnabledCodeEnum.class);
    }
}

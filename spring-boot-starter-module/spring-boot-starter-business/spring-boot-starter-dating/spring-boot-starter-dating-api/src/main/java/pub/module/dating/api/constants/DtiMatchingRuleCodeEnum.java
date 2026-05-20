package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DtiMatchingRuleCodeEnum implements BaseEnum {
    RECOMMEND("1", "推荐"),
    MATCHING("2", "匹配"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DtiMatchingRuleCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DtiMatchingRuleCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DtiMatchingRuleCodeEnum.class);
    }
}

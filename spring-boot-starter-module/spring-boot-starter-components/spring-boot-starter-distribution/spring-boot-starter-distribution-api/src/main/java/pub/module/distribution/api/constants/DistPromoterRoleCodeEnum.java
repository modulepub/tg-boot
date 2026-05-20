package pub.module.distribution.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DistPromoterRoleCodeEnum implements BaseEnum {
    NORMAL("normal", "普通用户"),
    MATCHMAKER("matchmaker", "红娘"),
    ANY("*", "不限角色"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DistPromoterRoleCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DistPromoterRoleCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DistPromoterRoleCodeEnum.class);
    }
}

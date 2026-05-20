package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = relationTypeCode */
@Getter
public enum RelationTypeCodeEnum implements BaseEnum {
    CUSTOMER_RELATION("1", "客户关系"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    RelationTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static RelationTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, RelationTypeCodeEnum.class);
    }

    @Deprecated
    public static RelationTypeCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, RelationTypeCodeEnum.class);
    }
}

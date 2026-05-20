package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusTagCode */
@Getter
public enum CusTagCodeEnum implements BaseEnum {
    QUALITY("1", "优质"),
    GOOD_COMMUNICATION("2", "沟通良好"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusTagCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusTagCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusTagCodeEnum.class);
    }

    @Deprecated
    public static CusTagCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusTagCodeEnum.class);
    }
}

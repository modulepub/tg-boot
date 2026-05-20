package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusHaveHouseStatusCode */
@Getter
public enum CusHaveHouseStatusCodeEnum implements BaseEnum {
    NO("0", "无"),
    YES("1", "有"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusHaveHouseStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusHaveHouseStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusHaveHouseStatusCodeEnum.class);
    }

    @Deprecated
    public static CusHaveHouseStatusCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusHaveHouseStatusCodeEnum.class);
    }
}

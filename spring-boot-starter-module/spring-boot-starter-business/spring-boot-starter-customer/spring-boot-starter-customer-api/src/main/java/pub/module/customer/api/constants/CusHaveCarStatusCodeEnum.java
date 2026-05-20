package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusHaveCarStatusCode */
@Getter
public enum CusHaveCarStatusCodeEnum implements BaseEnum {
    NO("0", "无"),
    YES("1", "有"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusHaveCarStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusHaveCarStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusHaveCarStatusCodeEnum.class);
    }

    @Deprecated
    public static CusHaveCarStatusCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusHaveCarStatusCodeEnum.class);
    }
}

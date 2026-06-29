package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusIdTypeCode */
@Getter
public enum CusIdTypeCodeEnum implements BaseEnum {
    ID_CARD("idCard", "身份证"),
    PASSPORT("passport", "护照"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusIdTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusIdTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusIdTypeCodeEnum.class);
    }

    @Deprecated
    public static CusIdTypeCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusIdTypeCodeEnum.class);
    }
}

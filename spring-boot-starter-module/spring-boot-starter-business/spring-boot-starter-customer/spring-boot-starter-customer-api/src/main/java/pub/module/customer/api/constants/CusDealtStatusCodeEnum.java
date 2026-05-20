package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusDealtStatusCode */
@Getter
public enum CusDealtStatusCodeEnum implements BaseEnum {
    NO("0", "未成交"),
    YES("1", "已成交"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusDealtStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusDealtStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusDealtStatusCodeEnum.class);
    }

    @Deprecated
    public static CusDealtStatusCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusDealtStatusCodeEnum.class);
    }
}

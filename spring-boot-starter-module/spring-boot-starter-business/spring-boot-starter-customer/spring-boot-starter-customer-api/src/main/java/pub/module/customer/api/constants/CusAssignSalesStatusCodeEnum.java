package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusAssignSalesStatusCode */
@Getter
public enum CusAssignSalesStatusCodeEnum implements BaseEnum {
    NOT_ASSIGNED("0", "未分配"),
    ASSIGNED("1", "已分配"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusAssignSalesStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusAssignSalesStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusAssignSalesStatusCodeEnum.class);
    }

    @Deprecated
    public static CusAssignSalesStatusCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusAssignSalesStatusCodeEnum.class);
    }
}

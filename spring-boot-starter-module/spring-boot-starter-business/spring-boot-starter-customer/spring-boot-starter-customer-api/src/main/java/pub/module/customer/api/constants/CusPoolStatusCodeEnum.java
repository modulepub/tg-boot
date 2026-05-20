package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusPoolStatusCode */
@Getter
public enum CusPoolStatusCodeEnum implements BaseEnum {
    NOT_IN_POOL("0", "未入库"),
    IN_POOL("1", "已入库"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusPoolStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusPoolStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusPoolStatusCodeEnum.class);
    }

    @Deprecated
    public static CusPoolStatusCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusPoolStatusCodeEnum.class);
    }
}

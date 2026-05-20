package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusDealtCompleteStatusCode */
@Getter
public enum CusDealtCompleteStatusCodeEnum implements BaseEnum {
    NO("0", "未完成"),
    YES("1", "已完成"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusDealtCompleteStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusDealtCompleteStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusDealtCompleteStatusCodeEnum.class);
    }

    @Deprecated
    public static CusDealtCompleteStatusCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusDealtCompleteStatusCodeEnum.class);
    }
}

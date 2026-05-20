package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusIntentionStatusCode */
@Getter
public enum CusIntentionStatusCodeEnum implements BaseEnum {
    NONE("0", "无意向"),
    HAS("1", "有意向"),
    MEDIUM("2", "中"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusIntentionStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusIntentionStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusIntentionStatusCodeEnum.class);
    }

    @Deprecated
    public static CusIntentionStatusCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusIntentionStatusCodeEnum.class);
    }
}

package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = promotionTaskTypeCode */
@Getter
public enum PromotionTaskTypeCodeEnum implements BaseEnum {
    CONTACT("contact", "联络"),
    SERVICE("service", "服务"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    PromotionTaskTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static PromotionTaskTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, PromotionTaskTypeCodeEnum.class);
    }

    @Deprecated
    public static PromotionTaskTypeCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, PromotionTaskTypeCodeEnum.class);
    }
}

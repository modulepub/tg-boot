package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = promotionTaskCode */
@Getter
public enum PromotionTaskCodeEnum implements BaseEnum {
    CONTACT_CUSTOMER("contactCustomer", "联络客户"),
    SERVE_CUSTOMERS("serveCustomers", "服务客户"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    PromotionTaskCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static PromotionTaskCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, PromotionTaskCodeEnum.class);
    }

    /** @deprecated 使用 {@link #fromJson(Object)} */
    @Deprecated
    public static PromotionTaskCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, PromotionTaskCodeEnum.class);
    }
}

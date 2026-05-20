package pub.module.distribution.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DistAccrualSourceTypeCodeEnum implements BaseEnum {
    ORDER_PAID("orderPaid", "订单支付"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DistAccrualSourceTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DistAccrualSourceTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DistAccrualSourceTypeCodeEnum.class);
    }
}

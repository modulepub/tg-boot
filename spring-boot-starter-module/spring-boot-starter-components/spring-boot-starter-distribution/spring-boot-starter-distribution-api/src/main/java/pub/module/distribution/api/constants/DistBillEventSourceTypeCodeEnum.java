package pub.module.distribution.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DistBillEventSourceTypeCodeEnum implements BaseEnum {
    ORDER_PAID("orderPaid", "订单支付"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DistBillEventSourceTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DistBillEventSourceTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DistBillEventSourceTypeCodeEnum.class);
    }
}

package pub.module.trade.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** 订单支付状态（入库多为 varchar 数字，比对时请配合 {@link String#valueOf(int)}） */
@Getter
public enum TdOdPaidStatusCodeEnum implements BaseEnum {
    NOT_PAID("0", "未支付"),
    PAID("1", "已支付"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    TdOdPaidStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static TdOdPaidStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, TdOdPaidStatusCodeEnum.class);
    }
}

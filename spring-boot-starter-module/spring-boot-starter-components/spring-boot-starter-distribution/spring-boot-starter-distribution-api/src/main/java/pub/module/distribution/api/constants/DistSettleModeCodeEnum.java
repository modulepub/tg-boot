package pub.module.distribution.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DistSettleModeCodeEnum implements BaseEnum {
    IMMEDIATE("immediate", "支付后立即结算"),
    ON_SERVICE_END("onServiceEnd", "服务期结束后结算"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DistSettleModeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DistSettleModeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DistSettleModeCodeEnum.class);
    }
}

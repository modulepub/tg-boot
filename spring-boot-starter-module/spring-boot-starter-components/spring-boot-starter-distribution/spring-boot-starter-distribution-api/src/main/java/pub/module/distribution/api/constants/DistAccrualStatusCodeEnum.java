package pub.module.distribution.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DistAccrualStatusCodeEnum implements BaseEnum {
    PENDING("pending", "服务期内待结算"),
    SETTLED("settled", "已结算可提现"),
    CANCELLED("cancelled", "已取消"),
    REVERSED("reversed", "已冲正"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DistAccrualStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DistAccrualStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DistAccrualStatusCodeEnum.class);
    }
}

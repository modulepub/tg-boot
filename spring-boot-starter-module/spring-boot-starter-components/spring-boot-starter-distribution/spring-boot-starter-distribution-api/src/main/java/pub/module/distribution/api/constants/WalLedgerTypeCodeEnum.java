package pub.module.distribution.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum WalLedgerTypeCodeEnum implements BaseEnum {
    ACCRUAL_SETTLE("accrualSettle", "佣金结算入账"),
    WITHDRAW_FREEZE("withdrawFreeze", "提现冻结"),
    WITHDRAW_SUCCESS("withdrawSuccess", "提现成功"),
    WITHDRAW_REJECT("withdrawReject", "提现驳回退回"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    WalLedgerTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static WalLedgerTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, WalLedgerTypeCodeEnum.class);
    }
}

package pub.module.trade.api.constants;

import lombok.Getter;

@Getter
public enum TdOdPaidStatusCodeEnum {

NOT_PAID("0", "未支付"),
PAID("1", "已支付"),
;
    private final String code;
    private final String desc;
    TdOdPaidStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

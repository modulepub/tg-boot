package pub.module.trade.api.constants;

import lombok.Getter;

@Getter
public enum TdOdPaidCodeEnum {

NOT_PAID("0", "未支付"),
PAID("1", "已支付"),
;
    private final String code;
    private final String desc;
    TdOdPaidCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

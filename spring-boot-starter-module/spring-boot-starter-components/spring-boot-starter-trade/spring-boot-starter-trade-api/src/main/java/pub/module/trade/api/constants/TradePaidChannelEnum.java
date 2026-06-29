package pub.module.trade.api.constants;

import lombok.Getter;

/**
 * 支付渠道编码（预支付 / 查单路由）。
 */
@Getter
public enum TradePaidChannelEnum {

    WX("wx", "微信支付"),
    WX_VIRTUAL("wxVirtual", "微信虚拟支付"),
    ;

    private final String code;
    private final String desc;

    TradePaidChannelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcProductCodeEnum {
    ZY_CL("cashLoan", "现金借贷"),
    ZY_SL("shoppingLoan", "先享后付"),
    ALIPAY("alipay", "支付宝"),
    WECHAT("wechatPay", "微信"),
    KQ("kqBankPay", "快钱银行卡付款"),
    ;
    public static final String ZY_SL_CODE = "shoppingLoan";
    public static final String KQ_STR = "kqBankPay";
    public static final String WECHAT_STR = "wechatPay";
    private final String code;
    private final String text;

    FcProductCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcProductTypeCodeEnum {
    JD("cashLoan", "现金借贷"),
    DB("shoppingLoan", "先享后付"),
    BANK("bankPay", "银行卡扣款"),
    WECHAT("wechatPay", "微信支付"),
    ;
    private final String code;
    private final String text;

    FcProductTypeCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}

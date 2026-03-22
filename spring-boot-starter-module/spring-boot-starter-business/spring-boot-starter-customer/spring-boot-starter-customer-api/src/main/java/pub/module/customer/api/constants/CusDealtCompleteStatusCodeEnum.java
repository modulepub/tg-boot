package pub.module.customer.api.constants;

import lombok.Getter;

@Getter
public enum CusDealtCompleteStatusCodeEnum {
    NO("0","未成交"),
    YES("1","已成交"),
    ;
    private final String code;
    private final String text;

    CusDealtCompleteStatusCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
}

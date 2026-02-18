package pub.module.customer.api.constants;

import lombok.Getter;

@Getter
public enum CusIntentionStatusCodeEnum {
    NO("0","无意向"),
    YES("1","有意向"),
    ;
    private final String code;
    private final String text;

    CusIntentionStatusCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
}

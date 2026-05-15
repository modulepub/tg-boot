package pub.module.customer.api.constants;

import lombok.Getter;

@Getter
public enum ContactPassedStatusCodeEnum {
    NO("0","未通过"),
    YES("1","已通过"),
    ;
    private final String code;
    private final String text;

    ContactPassedStatusCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
}

package pub.module.customer.api.constants;

import lombok.Getter;

@Getter
public enum CusFollowUpStatusCodeEnum {
    NO("0","未跟进"),
    YES("1","已跟进"),
    ;
    private final String code;
    private final String text;

    CusFollowUpStatusCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
}

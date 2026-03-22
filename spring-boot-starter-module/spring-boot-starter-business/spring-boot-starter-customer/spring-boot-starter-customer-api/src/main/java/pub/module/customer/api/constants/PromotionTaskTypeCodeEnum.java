package pub.module.customer.api.constants;

import lombok.Getter;

@Getter
public enum PromotionTaskTypeCodeEnum {
    CONTACT("contact","联络"),
    SERVICE("service","服务"),
    ;
    private final String code;
    private final String text;

    PromotionTaskTypeCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
}

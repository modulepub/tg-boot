package pub.module.customer.api.constants;

import lombok.Getter;

@Getter
public enum PromotionTaskCodeEnum {
    SC("serveCustomers","客服任务"),
    CC("contactCustomer","营销任务"),
    ;
    private final String code;
    private final String text;

    PromotionTaskCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
}

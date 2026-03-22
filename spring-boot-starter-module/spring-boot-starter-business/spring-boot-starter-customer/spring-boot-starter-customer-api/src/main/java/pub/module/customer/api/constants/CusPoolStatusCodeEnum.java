package pub.module.customer.api.constants;

import lombok.Getter;

@Getter
public enum CusPoolStatusCodeEnum {
    NO("0","未入库"),
    YES("1","已入库"),
            ;
    private final String code;
    private final String text;

    CusPoolStatusCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
}

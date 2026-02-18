package pub.module.customer.api.constants;

import lombok.Getter;

@Getter
public enum CusAssignServersStatusCodeEnum {
    NO("0","未分配客服"),
    YES("1","已分配客服"),
    ;
    private final String code;
    private final String text;

    CusAssignServersStatusCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
}

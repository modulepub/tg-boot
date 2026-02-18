package pub.module.customer.api.constants;


import lombok.Getter;

@Getter
public enum CusSourceEnum {

   EXCEL("1","EXCEL导入"),
   SELF_REGISTER("2","自由注册"),
    ;
    private final String code;
    private final String text;

    CusSourceEnum(String code, String text){
        this.code = code;
        this.text = text;
    }

}

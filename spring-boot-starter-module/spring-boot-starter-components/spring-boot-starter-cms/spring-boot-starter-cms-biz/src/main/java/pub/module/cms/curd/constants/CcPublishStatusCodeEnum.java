package pub.module.cms.curd.constants;


import lombok.Getter;

@Getter
public enum CcPublishStatusCodeEnum {

   PUBLISHED("1","已发布"),
   NOT_PUBLISHED("0","未发布"),
    ;
    private final String code;
    private final String text;

    CcPublishStatusCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }

}

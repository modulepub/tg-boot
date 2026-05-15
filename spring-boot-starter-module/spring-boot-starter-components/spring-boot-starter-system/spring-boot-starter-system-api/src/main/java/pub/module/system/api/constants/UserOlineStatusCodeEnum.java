package pub.module.system.api.constants;

import lombok.Getter;

@Getter
public enum UserOlineStatusCodeEnum {
    YES("1", "在线"),
    SMS("0", "不在线"),
    ;

    UserOlineStatusCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
    private final String code;

    private final String text;
}

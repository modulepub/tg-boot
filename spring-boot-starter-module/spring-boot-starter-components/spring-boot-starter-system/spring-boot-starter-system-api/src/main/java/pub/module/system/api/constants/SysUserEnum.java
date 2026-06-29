package pub.module.system.api.constants;

import lombok.Getter;

@Getter
public enum SysUserEnum {
    PREFIX_SMS_CODE("phone_msg", "短信REDIS KEY"),
    SUPPER_USER("admin", "超级管理员编码"),
;

    SysUserEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
    private final String code;

    private final String text;

}

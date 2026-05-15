package pub.module.system.api.constants;

import lombok.Getter;

@Getter
public enum VerificationTypeCodeEnum {
    CAPTCHA("CAPTCHA", "图片验证码"),
    SMS("SMS", "短信验证码"),
    LOGIN_ACCESS_TOKEN("LOGIN_ACCESS_TOKEN", "登录令牌"),
    ;

    VerificationTypeCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
    private final String code;

    private final String text;
}

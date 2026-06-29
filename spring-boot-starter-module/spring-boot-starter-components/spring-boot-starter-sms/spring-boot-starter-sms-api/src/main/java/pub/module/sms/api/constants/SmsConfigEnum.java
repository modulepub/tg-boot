package pub.module.sms.api.constants;

import lombok.Getter;

/**
 * 短信模板引擎
 */
@Getter
public enum SmsConfigEnum {
    LOGIN_SMS("loginSms", "通用验证码"),
    LOGIN_SMS_SERVICE("mosSmsSdk", "发送短信的实现服务"),
    ;


    private final String code;

    private final String desc;

    SmsConfigEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}

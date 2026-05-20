package pub.module.system.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum VerificationTypeCodeEnum implements BaseEnum {
    CAPTCHA("CAPTCHA", "图片验证码"),
    SMS("SMS", "短信验证码"),
    LOGIN_ACCESS_TOKEN("LOGIN_ACCESS_TOKEN", "登录令牌"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    VerificationTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static VerificationTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, VerificationTypeCodeEnum.class);
    }
}

package pub.module.system.api.constants;


import pub.module.common.enums.BaseEnum;

/**
 * 系统模块错误码枚举
 */
public enum SysErrorCodeEnum implements BaseEnum {

    SMS_CODE_ERROR("ES001", "验证码错误"),
    USER_NICK_EXIST_ERROR("ES002", "昵称已经存在"),
    PHONE_ALREADY_EXISTS("ES003", "该手机号已被使用")
    ;

    private final String code;
    private final String desc;

    SysErrorCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}

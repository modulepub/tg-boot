package pub.module.sms.api.constants;

import lombok.Getter;

/**
 * 短信场景枚举
 * 优化说明：简化枚举结构，Redis键前缀通过配置文件管理
 */
@Getter
public enum SmsType {

    /** 通用短信验证码 */
    COMMON("common","common", "通用验证码"),

    /** 登录场景验证码 */
    LOGIN("login", "login", "登录验证码"),

    /** 注册场景验证码 */
    REGISTER("register", "register","注册验证码"),

    /** 支付场景验证码 */
    PAY("pay", "pay","支付验证码");

    /** 场景标识 */
    private final String scene;

    /** 模板键（对应配置文件中的键名） */
    private final String templateKey;

    /** 场景描述 */
    private final String desc;

    SmsType(String scene, String templateKey, String desc) {
        this.scene = scene;
        this.templateKey = templateKey;
        this.desc = desc;
    }

    //    /**
    //     * 根据场景标识获取枚举
    //     */
    //    public static SmsType of(String scene) {
    //        for (SmsType smsEnum : values()) {
    //            if (smsEnum.scene.equals(scene)) {
    //                return smsEnum;
    //            }
    //        }
    //        return null;
    //    }
}

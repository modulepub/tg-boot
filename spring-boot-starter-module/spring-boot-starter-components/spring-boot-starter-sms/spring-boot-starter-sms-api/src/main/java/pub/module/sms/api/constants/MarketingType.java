package pub.module.sms.api.constants;

import lombok.Getter;

/**
 * 营销短信类型枚举
 * 优化说明：添加描述和模板键，简化代码结构
 */
@Getter
public enum MarketingType {
    /** 信用资质预审营销 */
    CREDIT_PRE_APPROVAL(1, "credit-pre-approval", "信用资质预审营销"),
    /** 特批通道营销 */
    SPECIAL_APPROVAL(2, "special-approval", "特批通道营销");

    /** 类型编码 */
    private final int code;
    /** 模板键（对应配置文件中的键名） */
    private final String templateKey;
    /** 类型描述 */
    private final String desc;

    MarketingType(int code, String templateKey, String desc) {
        this.code = code;
        this.templateKey = templateKey;
        this.desc = desc;
    }

    //    /**
    //     * 根据code获取枚举
    //     */
    //    public static MarketingType of(int code) {
    //        for (MarketingType type : values()) {
    //            if (type.code == code) {
    //                return type;
    //            }
    //        }
    //        return null;
    //    }
}

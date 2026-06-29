package pub.module.sms.api.constants;

import lombok.Getter;

/**
 * 通知短信类型枚举
 * 优化说明：添加描述和模板键，简化代码结构
 */
@Getter
public enum NoticeType {
    /** 额度待激活通知 */
    QUOTA_ACTIVATION(1, "quota-activation", "额度待激活通知"),
    /** 资质预审成功通知 */
    QUALIFICATION_SUCCESS(2, "qualification-success", "资质预审成功通知"),
    /** 额度审核通过通知 */
    QUOTA_APPROVED(3, "quota-approved", "额度审核通过通知"),
    /** 借款放款通知 */
    LOAN_DISBURSEMENT(4, "loan-disbursement", "借款放款通知");

    /** 类型编码 */
    private final int code;
    /** 模板键（对应配置文件中的键名） */
    private final String templateKey;
    /** 类型描述 */
    private final String desc;

    NoticeType(int code, String templateKey, String desc) {
        this.code = code;
        this.templateKey = templateKey;
        this.desc = desc;
    }

    //    /**
    //     * 根据code获取枚举
    //     */
    //    public static NoticeType of(int code) {
    //        for (NoticeType type : values()) {
    //            if (type.code == code) {
    //                return type;
    //            }
    //        }
    //        return null;
    //    }
}

package pub.module.dating.api.constants;

import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * 会员权益消费失败错误码（与 dating 模块 E100x 对齐，供前端识别）。
 */
@Getter
public enum CusMemberBenefitErrorCodeEnum implements BaseEnum {

    E1004("E1004", "添加好友次数不足"),
    E1001("E1001", "牵线次数不足"),
    E1006("E1006", "今日添加好友次数已用完，请升级会员"),
    E1007("E1007", "今日牵线次数已用完，请升级会员"),
    E1008("E1008", "今日推荐次数已用完，请升级会员"),
    E1009("E1009", "推荐次数不足"),
    ;

    private final String code;
    private final String desc;

    CusMemberBenefitErrorCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

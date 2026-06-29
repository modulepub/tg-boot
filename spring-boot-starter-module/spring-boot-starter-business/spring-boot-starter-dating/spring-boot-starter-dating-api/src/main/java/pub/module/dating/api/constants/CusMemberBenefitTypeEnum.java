package pub.module.dating.api.constants;

import lombok.Getter;

/**
 * 会员权益消费类型。
 */
@Getter
public enum CusMemberBenefitTypeEnum {

    ADD_FRIEND("addFriend", "添加好友"),
    RECOMMEND("recommend", "推荐"),
    MATCH("match", "牵线");

    private final String code;
    private final String desc;

    CusMemberBenefitTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CusMemberBenefitTypeEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (CusMemberBenefitTypeEnum value : values()) {
            if (value.code.equals(code.trim())) {
                return value;
            }
        }
        return null;
    }
}

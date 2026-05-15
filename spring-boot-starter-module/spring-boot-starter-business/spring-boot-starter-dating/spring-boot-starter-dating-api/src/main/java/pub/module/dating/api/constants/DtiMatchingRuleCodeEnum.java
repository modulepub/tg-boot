package pub.module.dating.api.constants;

import lombok.Getter;

@Getter
public enum DtiMatchingRuleCodeEnum {
    RECOMMEND("1", "推荐"),
    MATCHING ("2", "匹配"),
    ;
    private final String code;
    private final String desc;

    DtiMatchingRuleCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

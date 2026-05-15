package pub.module.dating.api.constants;

import lombok.Getter;

@Getter
public enum DtLikeDegreeCodeEnum {
    like("1", "喜欢"),
    NOT_LIKE ("0", "不喜欢"),
    ;
    private final String code;
    private final String desc;

    DtLikeDegreeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

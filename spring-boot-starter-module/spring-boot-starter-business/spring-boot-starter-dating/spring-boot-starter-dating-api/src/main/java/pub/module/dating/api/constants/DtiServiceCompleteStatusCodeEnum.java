package pub.module.dating.api.constants;

import lombok.Getter;

@Getter
public enum DtiServiceCompleteStatusCodeEnum {
    COMPLETED("1", "推荐"),
    NOT_COMPLETED ("2", "匹配"),
    ;
    private final String code;
    private final String desc;

    DtiServiceCompleteStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

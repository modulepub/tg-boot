package pub.module.dating.api.constants;

import lombok.Getter;

@Getter
public enum DtMaServiceStatusCodeEnum {
    ING("1", "服务中"),
    MATCHING ("2", "匹配"),
    ;
    private final String code;
    private final String desc;

    DtMaServiceStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

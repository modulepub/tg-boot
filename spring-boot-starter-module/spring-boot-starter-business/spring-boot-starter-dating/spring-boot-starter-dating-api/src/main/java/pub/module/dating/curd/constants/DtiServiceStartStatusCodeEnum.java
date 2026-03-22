package pub.module.dating.curd.constants;

import lombok.Getter;

@Getter
public enum DtiServiceStartStatusCodeEnum {
    STARTED("1", "已开始"),
    NOT_STARTED ("0", "未开始"),
    ;
    private final String code;
    private final String desc;

    DtiServiceStartStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

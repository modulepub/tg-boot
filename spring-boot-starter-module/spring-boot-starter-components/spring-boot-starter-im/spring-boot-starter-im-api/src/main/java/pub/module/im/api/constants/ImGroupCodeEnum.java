package pub.module.im.api.constants;

import lombok.Getter;

@Getter
public enum ImGroupCodeEnum {
    NOTICE("0000", "系统消息");

    private final String code;
    private final String desc;
    ImGroupCodeEnum(String code, String desc){
        this.code=code;
        this.desc=desc;
    }
}

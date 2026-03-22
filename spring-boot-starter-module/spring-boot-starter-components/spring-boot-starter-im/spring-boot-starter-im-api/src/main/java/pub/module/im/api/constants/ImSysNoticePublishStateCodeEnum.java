package pub.module.im.api.constants;

import lombok.Getter;

@Getter
public enum ImSysNoticePublishStateCodeEnum {
    NOT("0", "未发布"),
    YES("1", "已发布"),

    ;

    private final String code;
    private final String name;
    ImSysNoticePublishStateCodeEnum(String code, String name){
        this.code=code;
        this.name=name;
    }
}

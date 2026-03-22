package pub.module.im.api.constants;

import lombok.Getter;

@Getter
public enum ImGroupWorkStatusCodeEnum {
    FM("0", "繁忙中"),
    KX("1", "空闲中"),
    XX("2", "休息中"),

    ;

    private final String code;
    private final String name;
    ImGroupWorkStatusCodeEnum(String code, String name){
        this.code=code;
        this.name=name;
    }
}

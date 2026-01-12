package pub.module.dating.curd.constants;

import lombok.Getter;

@Getter
public enum DtCusSexCodeEnum {
    MAN("1", "男"),
    WOMAN ("2", "女"),
    ;
    private final String code;
    private final String desc;

    DtCusSexCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

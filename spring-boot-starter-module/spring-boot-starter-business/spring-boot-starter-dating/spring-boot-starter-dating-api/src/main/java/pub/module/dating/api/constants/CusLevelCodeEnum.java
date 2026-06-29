package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusLevelCode */
@Getter
public enum CusLevelCodeEnum implements BaseEnum {
    NORMAL("1", "普通客户"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusLevelCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusLevelCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusLevelCodeEnum.class);
    }

    @Deprecated
    public static CusLevelCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusLevelCodeEnum.class);
    }
}

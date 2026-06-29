package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusKinshipCode */
@Getter
public enum CusKinshipCodeEnum implements BaseEnum {
    SELF("self", "本人"),
    PARENT("parent", "家长"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusKinshipCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusKinshipCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusKinshipCodeEnum.class);
    }
}

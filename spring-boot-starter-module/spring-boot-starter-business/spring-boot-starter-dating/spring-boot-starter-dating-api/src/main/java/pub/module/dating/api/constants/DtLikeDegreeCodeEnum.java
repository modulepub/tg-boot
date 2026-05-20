package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DtLikeDegreeCodeEnum implements BaseEnum {
    NOT_LIKE("0", "不喜欢"),
    LIKE("1", "喜欢"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DtLikeDegreeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DtLikeDegreeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DtLikeDegreeCodeEnum.class);
    }
}

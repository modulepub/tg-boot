package pub.module.distribution.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DistBizLineCodeEnum implements BaseEnum {
    DATING("dating", "婚恋交友"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DistBizLineCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DistBizLineCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DistBizLineCodeEnum.class);
    }
}

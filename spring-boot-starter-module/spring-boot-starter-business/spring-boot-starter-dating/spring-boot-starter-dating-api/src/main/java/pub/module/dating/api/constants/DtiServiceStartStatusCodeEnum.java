package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DtiServiceStartStatusCodeEnum implements BaseEnum {
    NOT_STARTED("0", "未开始"),
    STARTED("1", "已开始"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DtiServiceStartStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DtiServiceStartStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DtiServiceStartStatusCodeEnum.class);
    }
}

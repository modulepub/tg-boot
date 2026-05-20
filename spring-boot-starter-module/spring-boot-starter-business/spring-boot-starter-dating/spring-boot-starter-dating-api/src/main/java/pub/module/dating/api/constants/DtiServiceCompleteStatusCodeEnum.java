package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DtiServiceCompleteStatusCodeEnum implements BaseEnum {
    COMPLETED("1", "推荐"),
    NOT_COMPLETED("2", "匹配"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DtiServiceCompleteStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DtiServiceCompleteStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DtiServiceCompleteStatusCodeEnum.class);
    }
}

package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DtMaServiceStatusCodeEnum implements BaseEnum {
    ING("1", "服务中"),
    MATCHING("2", "匹配"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DtMaServiceStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DtMaServiceStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DtMaServiceStatusCodeEnum.class);
    }
}

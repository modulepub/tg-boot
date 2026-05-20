package pub.module.distribution.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DistPeriodStatusCodeEnum implements BaseEnum {
    ACTIVE("active", "服务中"),
    ENDED("ended", "已结束"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DistPeriodStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DistPeriodStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DistPeriodStatusCodeEnum.class);
    }
}

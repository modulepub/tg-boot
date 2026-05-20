package pub.module.cms.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
@AllArgsConstructor
public enum ShortUrlStatusCodeEnum implements BaseEnum {
    DISABLED("0", "禁用"),
    ENABLED("1", "启用"),
    ;

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;
}

package pub.module.im.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * IM 消息类型
 */
@Getter
@AllArgsConstructor
public enum ImMessageTypeCodeEnum implements BaseEnum {
    TEXT("text", "文本"),
    RICH("rich", "图文");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public static ImMessageTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, ImMessageTypeCodeEnum.class);
    }
}

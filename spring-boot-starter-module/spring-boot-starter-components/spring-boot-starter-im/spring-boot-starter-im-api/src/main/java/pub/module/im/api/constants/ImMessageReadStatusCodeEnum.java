package pub.module.im.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * IM 消息已读状态
 */
@Getter
@AllArgsConstructor
public enum ImMessageReadStatusCodeEnum implements BaseEnum {
    UNREAD("0", "未读"),
    READ("1", "已读");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public static ImMessageReadStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, ImMessageReadStatusCodeEnum.class);
    }
}

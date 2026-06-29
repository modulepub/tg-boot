package pub.module.im.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * IM 好友状态
 */
@Getter
@AllArgsConstructor
public enum ImFriendStatusCodeEnum implements BaseEnum {
    DELETED("0", "删除"),
    NORMAL("1", "正常");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public static ImFriendStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, ImFriendStatusCodeEnum.class);
    }
}

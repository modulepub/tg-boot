package pub.module.im.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * IM 用户会话状态
 */
@Getter
@AllArgsConstructor
public enum ImUserSessionStatusCodeEnum implements BaseEnum {
    OFFLINE("0", "离线"),
    ONLINE("1", "在线");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public static ImUserSessionStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, ImUserSessionStatusCodeEnum.class);
    }
}

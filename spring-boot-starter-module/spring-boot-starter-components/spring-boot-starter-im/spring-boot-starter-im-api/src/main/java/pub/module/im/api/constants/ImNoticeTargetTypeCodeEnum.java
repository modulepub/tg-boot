package pub.module.im.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * IM 通知发送范围
 */
@Getter
@AllArgsConstructor
public enum ImNoticeTargetTypeCodeEnum implements BaseEnum {
    ALL("all", "全员");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public static ImNoticeTargetTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, ImNoticeTargetTypeCodeEnum.class);
    }
}

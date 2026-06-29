package pub.module.im.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * IM 通知发布状态
 */
@Getter
@AllArgsConstructor
public enum ImNoticePublishStateCodeEnum implements BaseEnum {
    DRAFT("0", "草稿"),
    SENT("1", "已发送");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    @JsonCreator
    public static ImNoticePublishStateCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, ImNoticePublishStateCodeEnum.class);
    }
}

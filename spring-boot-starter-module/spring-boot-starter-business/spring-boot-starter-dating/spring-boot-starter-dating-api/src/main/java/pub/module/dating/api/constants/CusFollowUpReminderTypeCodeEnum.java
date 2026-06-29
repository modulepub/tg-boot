package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusFollowUpReminderTypeCode */
@Getter
public enum CusFollowUpReminderTypeCodeEnum implements BaseEnum {
    PENDING("1", "待跟进"),
    THREE_TO_SIX_MONTHS("2", "未跟进3-6个月"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusFollowUpReminderTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusFollowUpReminderTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusFollowUpReminderTypeCodeEnum.class);
    }

    @Deprecated
    public static CusFollowUpReminderTypeCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusFollowUpReminderTypeCodeEnum.class);
    }
}

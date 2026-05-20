package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusFollowUpStatusCode */
@Getter
public enum CusFollowUpStatusCodeEnum implements BaseEnum {
    NO("0", "未跟进"),
    YES("1", "已跟进"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusFollowUpStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusFollowUpStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusFollowUpStatusCodeEnum.class);
    }

    @Deprecated
    public static CusFollowUpStatusCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusFollowUpStatusCodeEnum.class);
    }
}

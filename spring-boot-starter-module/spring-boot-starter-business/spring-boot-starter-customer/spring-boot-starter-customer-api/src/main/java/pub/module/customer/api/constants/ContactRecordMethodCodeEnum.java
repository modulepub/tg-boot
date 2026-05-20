package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = contactRecordMethodCode */
@Getter
public enum ContactRecordMethodCodeEnum implements BaseEnum {
    PHONE("1", "电话"),
    PERSONAL_WECHAT("2", "个人微信"),
    WORK_WECHAT("3", "企业微信"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    ContactRecordMethodCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static ContactRecordMethodCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, ContactRecordMethodCodeEnum.class);
    }

    @Deprecated
    public static ContactRecordMethodCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, ContactRecordMethodCodeEnum.class);
    }
}

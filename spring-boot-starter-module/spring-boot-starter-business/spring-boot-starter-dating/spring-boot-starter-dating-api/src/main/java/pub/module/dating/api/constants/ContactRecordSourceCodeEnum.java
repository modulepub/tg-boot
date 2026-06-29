package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = contactRecordSourceCode */
@Getter
public enum ContactRecordSourceCodeEnum implements BaseEnum {
    MANUAL("1", "手动录入"),
    CALL_SYSTEM("2", "通话系统"),
    WORK_WECHAT("3", "企业微信"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    ContactRecordSourceCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static ContactRecordSourceCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, ContactRecordSourceCodeEnum.class);
    }

    @Deprecated
    public static ContactRecordSourceCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, ContactRecordSourceCodeEnum.class);
    }
}

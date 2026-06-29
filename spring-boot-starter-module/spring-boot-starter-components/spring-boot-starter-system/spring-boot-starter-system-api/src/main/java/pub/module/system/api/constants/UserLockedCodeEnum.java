package pub.module.system.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = userLockedCode */
@Getter
public enum UserLockedCodeEnum implements BaseEnum {
    NO("0", "未锁定"),
    YES("1", "已锁定"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    UserLockedCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static UserLockedCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, UserLockedCodeEnum.class);
    }
}

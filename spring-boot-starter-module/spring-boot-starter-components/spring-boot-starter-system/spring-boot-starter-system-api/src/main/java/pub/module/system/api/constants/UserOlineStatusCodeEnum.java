package pub.module.system.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum UserOlineStatusCodeEnum implements BaseEnum {
    OFFLINE("0", "不在线"),
    YES("1", "在线"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    UserOlineStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static UserOlineStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, UserOlineStatusCodeEnum.class);
    }
}

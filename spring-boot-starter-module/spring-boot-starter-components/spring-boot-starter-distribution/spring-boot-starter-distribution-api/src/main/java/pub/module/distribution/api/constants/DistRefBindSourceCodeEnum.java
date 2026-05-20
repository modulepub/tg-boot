package pub.module.distribution.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DistRefBindSourceCodeEnum implements BaseEnum {
    REGISTER("register", "注册绑定"),
    SYNC("sync", "从用户表同步"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DistRefBindSourceCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DistRefBindSourceCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DistRefBindSourceCodeEnum.class);
    }
}

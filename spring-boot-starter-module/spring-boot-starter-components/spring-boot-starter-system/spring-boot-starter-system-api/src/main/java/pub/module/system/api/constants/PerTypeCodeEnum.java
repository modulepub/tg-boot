package pub.module.system.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** 权限类型：0 菜单 / 1 按钮 / 2 接口 */
@Getter
public enum PerTypeCodeEnum implements BaseEnum {
    MENU("0", "菜单"),
    BUTTON("1", "按钮"),
    API("2", "接口"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    PerTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static PerTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, PerTypeCodeEnum.class);
    }
}

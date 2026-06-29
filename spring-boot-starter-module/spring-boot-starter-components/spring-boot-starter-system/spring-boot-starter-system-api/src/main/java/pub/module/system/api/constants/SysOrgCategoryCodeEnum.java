package pub.module.system.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum SysOrgCategoryCodeEnum implements BaseEnum {
    COM("com", "公司"),
    DEPT("dept", "部门"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    SysOrgCategoryCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static SysOrgCategoryCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, SysOrgCategoryCodeEnum.class);
    }
}

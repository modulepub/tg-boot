package pub.module.customer.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum CusSourceEnum implements BaseEnum {
    EXCEL("1", "EXCEL导入"),
    SELF_REGISTER("2", "自主注册"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusSourceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusSourceEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusSourceEnum.class);
    }
}

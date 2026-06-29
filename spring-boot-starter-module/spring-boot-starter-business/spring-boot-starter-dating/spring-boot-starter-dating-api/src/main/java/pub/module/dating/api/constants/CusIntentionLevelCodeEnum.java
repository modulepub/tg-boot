package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = cusIntentionLevelCode */
@Getter
public enum CusIntentionLevelCodeEnum implements BaseEnum {
    HIGH("1", "高"),
    MEDIUM("2", "中"),
    LOW("3", "低"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusIntentionLevelCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusIntentionLevelCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, CusIntentionLevelCodeEnum.class);
    }

    @Deprecated
    public static CusIntentionLevelCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, CusIntentionLevelCodeEnum.class);
    }
}

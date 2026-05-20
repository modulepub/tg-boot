package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = relationPassedStatusCode（REJECTED 为业务扩展 -1） */
@Getter
public enum RelationPassedStatusCodeEnum implements BaseEnum {
    /** 字典项：不通过 */
    NO("0", "不通过"),
    /** 字典项：通过 */
    YES("1", "通过"),
    /** 业务驳回（持久化为 -1，非 dict_item 表项） */
    REJECTED("-1", "驳回"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    RelationPassedStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static RelationPassedStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, RelationPassedStatusCodeEnum.class);
    }

    @Deprecated
    public static RelationPassedStatusCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, RelationPassedStatusCodeEnum.class);
    }
}

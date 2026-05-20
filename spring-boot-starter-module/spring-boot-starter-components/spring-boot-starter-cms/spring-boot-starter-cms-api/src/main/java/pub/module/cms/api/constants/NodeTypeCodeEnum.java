package pub.module.cms.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = nodeTypeCode */
@Getter
public enum NodeTypeCodeEnum implements BaseEnum {
    CATALOG("catalog", "栏目"),
    DOCUMENT("document", "文章"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    NodeTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static NodeTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, NodeTypeCodeEnum.class);
    }

    @Deprecated
    public static NodeTypeCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, NodeTypeCodeEnum.class);
    }
}

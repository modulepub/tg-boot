package pub.module.cms.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = nodeContentTypeCode */
@Getter
public enum NodeContentTypeCodeEnum implements BaseEnum {
    TEXT("text", "文本"),
    LINK("link", "链接"),
    CITATION("citation", "引用"),
    FILE("file", "文件"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    NodeContentTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static NodeContentTypeCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, NodeContentTypeCodeEnum.class);
    }

    @Deprecated
    public static NodeContentTypeCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, NodeContentTypeCodeEnum.class);
    }
}

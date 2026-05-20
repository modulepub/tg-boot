package pub.module.cms.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = nodePublishStatusCode */
@Getter
public enum NodePublishStatusCodeEnum implements BaseEnum {
    NO("0", "否"),
    YES("1", "是"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    NodePublishStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static NodePublishStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, NodePublishStatusCodeEnum.class);
    }

    @Deprecated
    public static NodePublishStatusCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, NodePublishStatusCodeEnum.class);
    }
}

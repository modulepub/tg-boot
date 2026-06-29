package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = recommendedSourceCode */
@Getter
public enum RecommendedSourceCodeEnum implements BaseEnum {
    FREE("free", "免费推荐"),
    PAY("pay", "付费推荐"),
    MATCHMAKER("matchmaker", "红娘推荐"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    RecommendedSourceCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static RecommendedSourceCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, RecommendedSourceCodeEnum.class);
    }
}

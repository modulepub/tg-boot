package pub.module.verification.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * 内容审核流程（{@code cmRecordProcessCode}）：0待审核 1审核中 2审核结束
 */
@Getter
public enum CmRecordProcessCodeEnum implements BaseEnum {

    PENDING("0", "待审核"),
    REVIEWING("1", "审核中"),
    FINISHED("2", "审核结束"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CmRecordProcessCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CmRecordProcessCodeEnum fromJson(Object raw) {
        return parse(raw);
    }

    public static CmRecordProcessCodeEnum parse(Object raw) {
        return BaseEnum.parse(raw, CmRecordProcessCodeEnum.class);
    }

    public static CmRecordProcessCodeEnum effective(CmRecordProcessCodeEnum process) {
        return process != null ? process : PENDING;
    }
}

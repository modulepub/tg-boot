package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * 资质/入驻申请流程（{@code *ProcessCode} 字段）。
 * <p>
 * 对应字段：{@code mkIdentityProcessCode}、{@code mkCompanyIdentityProcessCode}
 */
@Getter
public enum IdentityApplyProcessCodeEnum implements BaseEnum {

    DRAFT("0", "待提交"),
    REVIEWING("1", "审核中"),
    APPROVED("2", "审核通过"),
    REJECTED("3", "审核拒绝"),
    PLATFORM_REVIEWING("4", "平台审核中"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    IdentityApplyProcessCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static IdentityApplyProcessCodeEnum fromJson(Object raw) {
        return parse(raw);
    }

    public static IdentityApplyProcessCodeEnum parse(Object raw) {
        return BaseEnum.parse(raw, IdentityApplyProcessCodeEnum.class);
    }

    /** null 视为待提交 */
    public static IdentityApplyProcessCodeEnum effective(IdentityApplyProcessCodeEnum process) {
        return process != null ? process : DRAFT;
    }

    public static String label(IdentityApplyProcessCodeEnum process) {
        return effective(process).getDesc();
    }

    public static boolean canSubmit(IdentityApplyProcessCodeEnum process) {
        IdentityApplyProcessCodeEnum p = effective(process);
        return p == DRAFT || p == REJECTED;
    }
}

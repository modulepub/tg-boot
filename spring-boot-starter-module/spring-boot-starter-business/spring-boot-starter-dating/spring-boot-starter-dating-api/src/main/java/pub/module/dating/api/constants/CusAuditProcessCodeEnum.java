package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * 客户资料审核流程（{@code cusAuditProcessCode}）。
 */
@Getter
public enum CusAuditProcessCodeEnum implements BaseEnum {

    PENDING_MODIFY("1", "待修改"),
    REVIEWING("2", "审核中"),
    APPROVED("3", "审核通过"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    CusAuditProcessCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CusAuditProcessCodeEnum fromJson(Object raw) {
        return parse(raw);
    }

    public static CusAuditProcessCodeEnum parse(Object raw) {
        return BaseEnum.parse(raw, CusAuditProcessCodeEnum.class);
    }

    public static String label(CusAuditProcessCodeEnum process) {
        return process == null ? "" : process.getDesc();
    }
}

package pub.module.system.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * 用户账号注销申请处理状态。
 */
@Getter
public enum SysUserCancellationProcessCodeEnum implements BaseEnum {
    PENDING("0", "待处理"),
    PROCESSED("1", "已处理"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    SysUserCancellationProcessCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static SysUserCancellationProcessCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, SysUserCancellationProcessCodeEnum.class);
    }

    @Deprecated
    public static SysUserCancellationProcessCodeEnum fromValue(String v) {
        return BaseEnum.parse(v, SysUserCancellationProcessCodeEnum.class);
    }
}

package pub.module.distribution.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum WalWithdrawStatusCodeEnum implements BaseEnum {
    PENDING("pending", "处理中"),
    SUCCESS("success", "已到账"),
    REJECTED("rejected", "已驳回"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    WalWithdrawStatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static WalWithdrawStatusCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, WalWithdrawStatusCodeEnum.class);
    }
}

package pub.module.dating.api.constants;

import lombok.Getter;
import pub.module.common.enums.ErrorCodeEnum;

@Getter
public enum DatingErrorCodeEnum implements ErrorCodeEnum {
    E1000("E1000", "当前用户客户信息不存在"),
    E1001("E1001", "牵线次数不足"),
    E1002("E1002", "双方已是好友，无需重复牵线"),
    E1003("E1003", "该牵线申请仍在沟通中，请勿重复提交"),
    ;
    private final String code;
    private final String desc;

    DatingErrorCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

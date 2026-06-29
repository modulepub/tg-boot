package pub.module.dating.api.constants;

import lombok.Getter;
import pub.module.common.enums.BaseEnum;

@Getter
public enum DatingErrorCodeEnum implements BaseEnum {
    E1000("E1000", "当前用户客户信息不存在"),
    E1001("E1001", "牵线次数不足"),
    E1002("E1002", "双方已是好友，无需重复牵线"),
    E1003("E1003", "该牵线申请仍在进行中，请勿重复提交"),
    E1005("E1005", "无权操作该牵线记录"),
    E1004("E1004", "添加好友次数不足"),
    E1006("E1006", "今日添加好友次数已用完，请升级会员"),
    E1007("E1007", "今日牵线次数已用完，请升级会员"),
    ;
    private final String code;
    private final String desc;

    DatingErrorCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

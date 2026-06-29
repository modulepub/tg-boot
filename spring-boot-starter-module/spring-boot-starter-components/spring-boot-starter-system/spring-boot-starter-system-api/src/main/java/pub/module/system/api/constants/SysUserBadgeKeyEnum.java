package pub.module.system.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * 用户角标 key 定义,业务模块定义在自己的即可，此处用作示例。
 */
@Getter
public enum SysUserBadgeKeyEnum implements BaseEnum {
    ME_RECOMMEND("me_recommend", "我的-推荐"),
    ME_LIKE_ME("me_like_me", "我的-喜欢我"),
    ME_MY_LIKE("me_my_like", "我的-我喜欢"),
    ME_CONTACT("me_contact", "我的-联系人"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    SysUserBadgeKeyEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static SysUserBadgeKeyEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, SysUserBadgeKeyEnum.class);
    }

    @Deprecated
    public static SysUserBadgeKeyEnum fromValue(String v) {
        return BaseEnum.parse(v, SysUserBadgeKeyEnum.class);
    }
}

package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = contactSourceCode */
@Getter
public enum ContactSourceCodeEnum implements BaseEnum {
    FRIEND_REQUEST("friendRequest", "添加好友"),
    DATING_EVENT("datingEvent", "相亲交友活动"),
    MATCHMAKER_MATCHING("matchmakerMatching", "红娘牵线"),
    MUTUAL_LIKE("mutualLike", "相互喜欢"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    ContactSourceCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static ContactSourceCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, ContactSourceCodeEnum.class);
    }
}

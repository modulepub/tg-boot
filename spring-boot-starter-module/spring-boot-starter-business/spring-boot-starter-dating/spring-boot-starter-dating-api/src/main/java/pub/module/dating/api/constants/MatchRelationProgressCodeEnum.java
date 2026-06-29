package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/** dict_code = matchRelationProgressCode，牵线关系进度 */
@Getter
public enum MatchRelationProgressCodeEnum implements BaseEnum {
    PENDING_COMMUNICATION("pendingCommunication", "待沟通"),
    COMMUNICATING("communicating", "沟通中"),
    MEETING_SCHEDULED("meetingScheduled", "已约见"),
    DEVELOPING("developing", "关系发展中"),
    ENDED("ended", "已结束"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    MatchRelationProgressCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static MatchRelationProgressCodeEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, MatchRelationProgressCodeEnum.class);
    }

    public static MatchRelationProgressCodeEnum parse(Object raw) {
        return BaseEnum.parse(raw, MatchRelationProgressCodeEnum.class);
    }

    public static String label(MatchRelationProgressCodeEnum progress) {
        return progress == null ? PENDING_COMMUNICATION.getDesc() : progress.getDesc();
    }

    /** 是否仍视为进行中（不可重复发起牵线） */
    public static boolean inProgress(MatchRelationProgressCodeEnum progress) {
        return progress == null || progress != ENDED;
    }
}

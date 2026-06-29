package pub.module.im.api.service.dto;

import lombok.Data;

@Data
public class ImAddFriendDTO {
    /**
     * 发起添加的一方
     */
    private String fromUserCode;
    /**
     * 被添加好友的一方
     */
    private String toUserCode;
    /**
     * 好友备注（可选）
     */
    private String remark;
    /**
     * 添加来源（可选）
     */
    private String addSource;
    /**
     * 添加附言（可选）
     */
    private String addWording;
}

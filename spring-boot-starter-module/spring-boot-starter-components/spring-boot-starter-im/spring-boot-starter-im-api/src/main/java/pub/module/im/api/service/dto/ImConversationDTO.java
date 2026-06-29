package pub.module.im.api.service.dto;

import lombok.Data;

/**
 * IM 会话 DTO
 */
@Data
public class ImConversationDTO {

    /**
     * 会话编码
     */
    private String conversationCode;

    /**
     * 对方用户编码
     */
    private String peerUserCode;

    /**
     * 对方昵称
     */
    private String peerNickName;

    /**
     * 对方头像
     */
    private String peerAvatar;

    /**
     * 最后一条消息内容
     */
    private String lastMessageContent;

    /**
     * 最后一条消息时间
     */
    private String lastMessageTime;

    /**
     * 最后活跃时间（优先取最后一条消息时间，用于列表排序）
     */
    private String lastActiveTime;

    /**
     * 未读数
     */
    private Integer unreadCount;
}

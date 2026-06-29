package pub.module.im.api.service.dto;

import lombok.Data;

/**
 * IM WebSocket 消息协议
 */
@Data
public class ImWebSocketMessageDTO {

    /**
     * 消息类型：text / rich / read_receipt / unread_count / system
     */
    private String type;

    /**
     * 发送方用户编码
     */
    private String fromUserCode;

    /**
     * 接收方用户编码
     */
    private String toUserCode;

    /**
     * 消息业务编码
     */
    private String messageCode;

    /**
     * 内容（文本或JSON字符串）
     */
    private String content;

    /**
     * 图文标题
     */
    private String title;

    /**
     * 图文图片
     */
    private String imageUrl;

    /**
     * 图文链接
     */
    private String linkUrl;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 未读数（unread_count类型使用）
     */
    private Integer unreadCount;

    /**
     * 会话编码
     */
    private String conversationCode;
}

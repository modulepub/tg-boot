package pub.module.im.api.service.dto;

import lombok.Data;

/**
 * IM 消息 DTO
 */
@Data
public class ImMessageDTO {

    /**
     * 消息业务编码
     */
    private String messageCode;

    /**
     * 会话编码
     */
    private String conversationCode;

    /**
     * 发送方用户编码
     */
    private String fromUserCode;

    /**
     * 接收方用户编码
     */
    private String toUserCode;

    /**
     * 消息类型 text/rich
     */
    private String typeCode;

    /**
     * 文本内容
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
     * 已读状态 0/1
     */
    private String readStatusCode;

    /**
     * 发送状态 0/1
     */
    private String sendStatusCode;

    /**
     * 创建时间
     */
    private String createTime;
}

package pub.module.im.api.service.dto;

import lombok.Data;

/**
 * IM 消息已读确认
 */
@Data
public class ImMessageReadDTO {

    /**
     * 消息业务编码列表
     */
    private java.util.List<String> messageCodes;

    /**
     * 发送方用户编码（用于回执）
     */
    private String fromUserCode;

    /**
     * 会话编码
     */
    private String conversationCode;
}

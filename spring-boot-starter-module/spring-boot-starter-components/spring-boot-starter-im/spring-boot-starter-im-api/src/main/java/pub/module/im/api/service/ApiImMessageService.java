package pub.module.im.api.service;

import pub.module.im.api.service.dto.ImMessageDTO;
import pub.module.im.api.service.dto.ImMessageReadDTO;
import pub.module.im.api.service.dto.ImMessageSendDTO;

import java.util.List;

/**
 * IM 消息服务
 */
public interface ApiImMessageService {

    /**
     * 发送消息
     */
    ImMessageDTO sendMessage(String fromUserCode, ImMessageSendDTO sendDTO);

    /**
     * 标记消息已读
     */
    void markRead(List<String> messageCodes, String toUserCode);

    /**
     * 获取会话消息列表
     */
    List<ImMessageDTO> listMessages(String conversationCode, long pageNo, long pageSize);

    /**
     * 获取用户未读消息总数
     */
    int getUnreadCount(String userCode);

    /**
     * 获取用户未读消息列表
     */
    List<ImMessageDTO> listUnreadMessages(String userCode);

    /**
     * 创建或获取会话
     */
    String getOrCreateConversation(String userACode, String userBCode);

    /**
     * 获取用户会话列表
     */
    List<?> listConversations(String userCode);

    /**
     * 管理端-获取指定用户参与的全部聊天记录（按时间正序）
     */
    List<ImMessageDTO> listMessagesByUserForMgt(String userCode, long pageNo, long pageSize);
}

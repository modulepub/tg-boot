package pub.module.im.biz.messaging;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pub.module.common.messaging.TransactionAfterCommit;
import pub.module.im.api.service.dto.ImMessageDTO;
import pub.module.im.api.service.dto.ImWebSocketMessageDTO;

/**
 * IM 消息事件发布器（事务提交后推送 WebSocket）。
 */
@Slf4j
@Service
public class ImMessageEventPublisher {

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 消息发送后推送 WebSocket（事务提交后执行）。
     */
    public void publishMessageSentAfterCommit(ImMessageDTO message) {
        if (message == null || StrUtil.isBlank(message.getToUserCode())) {
            return;
        }
        TransactionAfterCommit.runAfterCommit(() -> doPushMessage(message));
    }

    private void doPushMessage(ImMessageDTO message) {
        try {
            String toUserCode = message.getToUserCode();
            String fromUserCode = message.getFromUserCode();

            // 推送给接收方
            ImWebSocketMessageDTO pushMsg = buildWebSocketMessage(message);
            messagingTemplate.convertAndSendToUser(toUserCode, "/queue/messages", pushMsg);
            log.debug("WebSocket 消息已推送 to={}, type={}", toUserCode, message.getTypeCode());

            // 推送给发送方（多端同步）
            if (StrUtil.isNotBlank(fromUserCode)) {
                ImWebSocketMessageDTO senderMsg = buildWebSocketMessage(message);
                senderMsg.setType("self_" + senderMsg.getType());
                messagingTemplate.convertAndSendToUser(fromUserCode, "/queue/messages", senderMsg);
            }

            // 推送未读角标给接收方
            // 注：未读数在事务提交后可能已更新，这里推送触发前端刷新
            ImWebSocketMessageDTO unreadMsg = new ImWebSocketMessageDTO();
            unreadMsg.setType("unread_count");
            unreadMsg.setUnreadCount(null);
            messagingTemplate.convertAndSendToUser(toUserCode, "/queue/notifications", unreadMsg);

        } catch (Exception e) {
            log.error("WebSocket 推送消息失败 messageCode={}", message.getMessageCode(), e);
        }
    }

    private ImWebSocketMessageDTO buildWebSocketMessage(ImMessageDTO message) {
        ImWebSocketMessageDTO dto = new ImWebSocketMessageDTO();
        dto.setType(message.getTypeCode());
        dto.setFromUserCode(message.getFromUserCode());
        dto.setToUserCode(message.getToUserCode());
        dto.setMessageCode(message.getMessageCode());
        dto.setContent(message.getContent());
        dto.setTitle(message.getTitle());
        dto.setImageUrl(message.getImageUrl());
        dto.setLinkUrl(message.getLinkUrl());
        dto.setConversationCode(message.getConversationCode());
        dto.setTimestamp(System.currentTimeMillis());
        return dto;
    }
}

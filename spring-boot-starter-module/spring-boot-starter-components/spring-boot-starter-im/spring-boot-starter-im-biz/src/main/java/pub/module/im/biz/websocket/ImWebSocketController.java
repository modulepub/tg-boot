package pub.module.im.biz.websocket;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import pub.module.im.api.service.ApiImMessageService;
import pub.module.im.api.service.dto.ImMessageDTO;
import pub.module.im.api.service.dto.ImMessageReadDTO;
import pub.module.im.api.service.dto.ImMessageSendDTO;
import pub.module.im.api.service.dto.ImWebSocketMessageDTO;

import java.security.Principal;
import java.util.List;

/**
 * IM WebSocket STOMP 消息控制器
 */
@Slf4j
@Controller
public class ImWebSocketController {

    @Resource
    private SimpMessagingTemplate messagingTemplate;
    @Resource
    private ApiImMessageService apiImMessageService;
    @Resource
    private ImSessionManager sessionManager;

    /**
     * 发送消息
     */
    @MessageMapping("/im/send")
    public void sendMessage(@Payload ImMessageSendDTO dto, Principal principal) {
        String fromUserCode = principal.getName();
        log.debug("WebSocket 发送消息, from={}, to={}, type={}", fromUserCode, dto.getToUserCode(), dto.getTypeCode());

        try {
            // 保存消息并获取DTO（内部已包含事务提交后 WebSocket 推送）
            apiImMessageService.sendMessage(fromUserCode, dto);

        } catch (Exception e) {
            log.error("WebSocket 发送消息失败", e);
            // 发送错误通知给发送方
            ImWebSocketMessageDTO errorMsg = new ImWebSocketMessageDTO();
            errorMsg.setType("error");
            errorMsg.setContent(e.getMessage());
            messagingTemplate.convertAndSendToUser(fromUserCode, "/queue/notifications", errorMsg);
        }
    }

    /**
     * 标记消息已读
     */
    @MessageMapping("/im/read")
    public void readMessage(@Payload ImMessageReadDTO dto, Principal principal) {
        String toUserCode = principal.getName();
        log.debug("WebSocket 标记已读, user={}, messages={}", toUserCode, dto.getMessageCodes());

        try {
            apiImMessageService.markRead(dto.getMessageCodes(), toUserCode);

            // 推送已读回执给发送方
            if (StrUtil.isNotBlank(dto.getFromUserCode())) {
                ImWebSocketMessageDTO receipt = new ImWebSocketMessageDTO();
                receipt.setType("read_receipt");
                receipt.setFromUserCode(toUserCode);
                receipt.setToUserCode(dto.getFromUserCode());
                receipt.setConversationCode(dto.getConversationCode());
                receipt.setTimestamp(System.currentTimeMillis());
                messagingTemplate.convertAndSendToUser(dto.getFromUserCode(), "/queue/notifications", receipt);
            }

            // 推送更新后的未读角标给自己（所有端）
            int unreadCount = apiImMessageService.getUnreadCount(toUserCode);
            ImWebSocketMessageDTO unreadMsg = new ImWebSocketMessageDTO();
            unreadMsg.setType("unread_count");
            unreadMsg.setUnreadCount(unreadCount);
            messagingTemplate.convertAndSendToUser(toUserCode, "/queue/notifications", unreadMsg);

        } catch (Exception e) {
            log.error("WebSocket 标记已读失败", e);
        }
    }

    /**
     * 心跳检测
     */
    @MessageMapping("/im/ping")
    @SendToUser("/queue/pong")
    public ImWebSocketMessageDTO ping(Principal principal) {
        ImWebSocketMessageDTO pong = new ImWebSocketMessageDTO();
        pong.setType("pong");
        pong.setTimestamp(System.currentTimeMillis());
        return pong;
    }

    /**
     * 用户上线时推送初始数据
     */
    public void pushInitialData(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return;
        }
        try {
            // 推送未读角标
            int unreadCount = apiImMessageService.getUnreadCount(userCode);
            ImWebSocketMessageDTO unreadMsg = new ImWebSocketMessageDTO();
            unreadMsg.setType("unread_count");
            unreadMsg.setUnreadCount(unreadCount);
            messagingTemplate.convertAndSendToUser(userCode, "/queue/notifications", unreadMsg);

            // 推送未读消息列表
            List<ImMessageDTO> unreadMessages = apiImMessageService.listUnreadMessages(userCode);
            for (ImMessageDTO msg : unreadMessages) {
                ImWebSocketMessageDTO pushMsg = buildWebSocketMessage(msg);
                messagingTemplate.convertAndSendToUser(userCode, "/queue/messages", pushMsg);
            }
        } catch (Exception e) {
            log.error("推送初始数据失败, userCode={}", userCode, e);
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

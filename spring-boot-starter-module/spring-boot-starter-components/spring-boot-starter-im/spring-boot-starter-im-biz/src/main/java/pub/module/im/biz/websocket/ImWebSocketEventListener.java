package pub.module.im.biz.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import jakarta.annotation.Resource;
import java.security.Principal;

/**
 * WebSocket 连接/断开事件监听器
 */
@Slf4j
@Component
public class ImWebSocketEventListener {

    @Resource
    private ImSessionManager sessionManager;
    @Resource
    private ImWebSocketController webSocketController;

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userCode = resolveUserCode(accessor);
        String sessionId = accessor.getSessionId();
        if (userCode != null && sessionId != null) {
            sessionManager.register(userCode, sessionId);
            log.info("WebSocket 连接建立, userCode={}, sessionId={}", userCode, sessionId);
            webSocketController.pushInitialData(userCode);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        if (sessionId != null) {
            String userCode = sessionManager.getUserCodeBySession(sessionId);
            sessionManager.unregister(sessionId);
            log.info("WebSocket 连接断开, userCode={}, sessionId={}", userCode, sessionId);
        }
    }

    private String resolveUserCode(StompHeaderAccessor accessor) {
        Principal user = accessor.getUser();
        if (user != null && user.getName() != null) {
            return user.getName();
        }
        if (accessor.getSessionAttributes() != null) {
            Object value = accessor.getSessionAttributes().get(ImWebSocketInterceptor.SESSION_USER_CODE);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }
}

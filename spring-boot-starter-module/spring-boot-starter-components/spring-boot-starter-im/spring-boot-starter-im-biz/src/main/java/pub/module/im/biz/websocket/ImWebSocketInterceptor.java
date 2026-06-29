package pub.module.im.biz.websocket;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import pub.module.common.security.jwt.JwtSupport;
import pub.module.im.api.constants.ImSpecialUserConstants;

import java.security.Principal;
import java.util.Map;

/**
 * WebSocket 握手与 STOMP 通道拦截器：JWT 认证并绑定 Principal（userCode）。
 */
@Slf4j
@Component
public class ImWebSocketInterceptor implements HandshakeInterceptor, ChannelInterceptor {

    static final String SESSION_USER_CODE = "userCode";

    private final JwtSupport jwtSupport;

    public ImWebSocketInterceptor(JwtSupport jwtSupport) {
        this.jwtSupport = jwtSupport;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = extractToken(servletRequest);
            if (StrUtil.isNotBlank(token)) {
                try {
                    var decodedJWT = jwtSupport.verify(token);
                    String userCode = decodedJWT.getClaim("userCode").asString();
                    if (StrUtil.isNotBlank(userCode)) {
                        String effectiveUserCode = resolveEffectiveUserCode(servletRequest, userCode.trim());
                        attributes.put(SESSION_USER_CODE, effectiveUserCode);
                        log.debug("WebSocket 认证成功, userCode={}", effectiveUserCode);
                        return true;
                    }
                } catch (Exception e) {
                    log.warn("WebSocket Token 验证失败: {}", e.getMessage());
                }
            }
        }
        log.warn("WebSocket 连接被拒绝，缺少有效 Token");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String userCode = resolveUserCode(accessor);
            if (StrUtil.isNotBlank(userCode)) {
                accessor.setUser(new ImUserPrincipal(userCode.trim()));
            }
        }
        return message;
    }

    private String resolveUserCode(StompHeaderAccessor accessor) {
        if (accessor.getSessionAttributes() != null) {
            Object value = accessor.getSessionAttributes().get(SESSION_USER_CODE);
            if (value != null && StrUtil.isNotBlank(value.toString())) {
                return value.toString();
            }
        }
        Principal user = accessor.getUser();
        return user != null ? user.getName() : null;
    }

    private String resolveEffectiveUserCode(ServletServerHttpRequest request, String jwtUserCode) {
        String imUserCode = StrUtil.trim(request.getServletRequest().getParameter("imUserCode"));
        if (StrUtil.isNotBlank(imUserCode) && StrUtil.equals(imUserCode, ImSpecialUserConstants.MGT_SYSTEM_USER_CODE)) {
            return imUserCode;
        }
        return jwtUserCode;
    }

    private String extractToken(ServletServerHttpRequest request) {
        String token = request.getServletRequest().getParameter("token");
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        String authHeader = request.getServletRequest().getHeader("Authorization");
        if (StrUtil.isNotBlank(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    static final class ImUserPrincipal implements Principal {
        private final String userCode;

        ImUserPrincipal(String userCode) {
            this.userCode = userCode;
        }

        @Override
        public String getName() {
            return userCode;
        }
    }
}

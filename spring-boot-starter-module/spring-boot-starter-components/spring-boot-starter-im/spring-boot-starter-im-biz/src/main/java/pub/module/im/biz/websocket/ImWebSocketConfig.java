package pub.module.im.biz.websocket;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * IM WebSocket + STOMP 配置
 */
@Configuration
@EnableWebSocketMessageBroker
public class ImWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Resource
    private ImWebSocketInterceptor imWebSocketInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/im")
                .setAllowedOriginPatterns("*")
                .addInterceptors(imWebSocketInterceptor)
                .withSockJS();
        // 微信小程序等不支持 SockJS 的客户端使用原生 WebSocket
        registry.addEndpoint("/ws/im-native")
                .setAllowedOriginPatterns("*")
                .addInterceptors(imWebSocketInterceptor);
    }
}

package pub.module.im.biz.websocket;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 通道配置（注册 STOMP 拦截器）
 */
@Configuration
public class ImWebSocketChannelConfig implements WebSocketMessageBrokerConfigurer {

    @Resource
    private ImWebSocketInterceptor imWebSocketInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(imWebSocketInterceptor);
    }
}

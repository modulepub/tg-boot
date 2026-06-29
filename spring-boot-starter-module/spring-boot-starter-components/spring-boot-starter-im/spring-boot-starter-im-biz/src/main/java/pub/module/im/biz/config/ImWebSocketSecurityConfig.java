package pub.module.im.biz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

/**
 * WebSocket 握手放行：认证由 {@link pub.module.im.biz.websocket.ImWebSocketInterceptor} 处理。
 */
@Configuration
public class ImWebSocketSecurityConfig {

    @Bean
    public WebSecurityCustomizer imWebSocketSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/ws/im/**", "/ws/im-native", "/ws/im-native/**");
    }
}

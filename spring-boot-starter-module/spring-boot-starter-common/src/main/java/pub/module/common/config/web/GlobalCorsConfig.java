package pub.module.common.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * 全局跨域配置类
 * 配置跨域资源共享策略
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 1. 允许前端域名（开发环境是localhost:3000，生产替换为实际域名）
        config.addAllowedOriginPattern("*");
        // 2. 允许所有请求方法（GET/POST/OPTIONS等）
        config.addAllowedMethod("*");
        // 3. 允许所有请求头（包含Authorization）
        config.addAllowedHeader("*");
        // 4. 允许携带Cookie（若不需要可设为false）
        config.setAllowCredentials(true);
        // 5. 暴露前端需要的自定义响应头（如 Authorization）
        config.addExposedHeader("Authorization");
        // 5. 预请求缓存时间（避免频繁发OPTIONS）
        config.setMaxAge(3600L);

        // 绑定到所有接口
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
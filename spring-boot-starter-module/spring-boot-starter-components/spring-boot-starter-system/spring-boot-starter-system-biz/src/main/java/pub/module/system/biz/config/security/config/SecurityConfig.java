package pub.module.system.biz.config.security.config;

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pub.module.system.biz.config.security.filter.JwtAuthenticationFilter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security 配置类
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    public static String[] publicEndpoints = {"/pub/**" };
    public static String[] swaggerEndpoints = {"/swagger-ui/**" , "/doc.html/**" , "/webjars.html/**","/webjars/**", "/v3/**"};
    public static String[] staticEndpoints = { "/css/**" , "/docs/**"  , "/favicon.ico","/js/**" , "/fonts/**" , "/img/**" , "/ajax/**","/html/**"  };
    public static String[] genEndpoints = { "/tool/**" };

    static {
        publicEndpoints = ArrayUtil.addAll(publicEndpoints,swaggerEndpoints,staticEndpoints,genEndpoints);
    }
    // 注入CORS配置源（关联你之前的全局CORS配置，或直接在此定义）

    @Bean
    public AuthenticationManager authenticationManager() {
        CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider();
        return new ProviderManager(Collections.singletonList(customAuthenticationProvider));
    }

    /**
     * HTTP 过滤器链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 强制启用CORS，绑定全局CORS配置（关键！）
                // 2. 禁用CSRF（JWT模式不需要）
                .csrf(AbstractHttpConfigurer::disable)
                // 3. 无状态会话（JWT模式）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. 授权规则：先放行OPTIONS，再放公开接口
                .authorizeHttpRequests(auth -> auth
                        // 放行所有OPTIONS预请求（必须！）
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 放行你的公开接口（比如登录、验证码）
                        .requestMatchers(publicEndpoints).permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                .headers(headers -> // 三种可选配置（按需选择其一，推荐 sameOrigin）
                        headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
        ;

        // 添加 JWT 认证过滤器
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter();
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // 若有这行，说明Csrf是关闭的，排除此问题
        log.info("SecurityFilterChain jwt has been configured.");
        return http.build();
    }
    /**
     * 方法安全表达式处理器
     * 用于支持 @PreAuthorize 注解中的权限表达式
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return handler;
    }

    public static class CustomPermissionEvaluator implements PermissionEvaluator {


        @Override
        public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
            if (authentication == null || permission == null) {
                return false;
            }

            String permissionStr = permission.toString();

            // 获取用户权限
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            // 检查是否拥有该权限
            for (GrantedAuthority authority : authorities) {
                String authorityStr = authority.getAuthority();

                // 精确匹配权限
                if (authorityStr.equals(permissionStr)) {
                    return true;
                }

                // 支持通配符匹配，例如：sys:user:* 匹配 sys:user:list, sys:user:add 等
                if (matchesPermission(authorityStr, permissionStr)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
            // 支持基于资源ID和类型的权限验证
            // 例如：hasPermission(authentication, userId, "User", "read")
            return hasPermission(authentication, null, permission);
        }

        /**
         * 权限匹配方法，支持通配符
         * 例如：
         *sys:user:* 匹配 sys:user:list, sys:user:add 等
         *sys:* 匹配 sys:user:list, sys:role:add 等
         */
        private boolean matchesPermission(String pattern, String permission) {
            if (pattern.equals(permission)) {
                return true;
            }

            // 支持通配符匹配
            if (pattern.contains("*")) {
                String regex = pattern.replace("." , "\\.").replace("*" , ".*");
                return permission.matches(regex);
            }

            // 支持模块权限：如果 pattern 是模块前缀，permission 以该前缀开头则匹配
            // 例如：pattern = "sys:user", permission = "sys:user:list" -> true
            return permission.startsWith(pattern + ":");
        }

    }
}


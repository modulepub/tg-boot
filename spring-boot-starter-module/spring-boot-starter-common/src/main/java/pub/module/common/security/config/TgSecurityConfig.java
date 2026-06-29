package pub.module.common.security.config;

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import pub.module.common.security.filter.TgJwtAuthenticationFilter;
import pub.module.common.security.jwt.JwtSupport;
import pub.module.common.security.jwt.TgJwtProperties;
import pub.module.common.security.session.AuthSessionStore;

import java.util.Collections;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@EnableConfigurationProperties({TgJwtProperties.class, TgSecurityProperties.class})
public class TgSecurityConfig {

    public static String[] publicEndpoints = {"/pub/**"};
    public static String[] swaggerEndpoints = {"/swagger-ui/**", "/doc.html/**", "/webjars.html/**", "/webjars/**", "/v3/**"};
    public static String[] staticEndpoints = {"/css/**", "/docs/**", "/favicon.ico", "/js/**", "/fonts/**", "/img/**", "/ajax/**", "/html/**"};
    public static String[] genEndpoints = {"/tool/**"};

    static {
        publicEndpoints = ArrayUtil.addAll(publicEndpoints, swaggerEndpoints, staticEndpoints, genEndpoints);
    }

    private final JwtSupport jwtSupport;
    private final AuthSessionStore authSessionStore;
    private final TgSecurityProperties securityProperties;

    public TgSecurityConfig(JwtSupport jwtSupport, AuthSessionStore authSessionStore, TgSecurityProperties securityProperties) {
        this.jwtSupport = jwtSupport;
        this.authSessionStore = authSessionStore;
        this.securityProperties = securityProperties;
        if (securityProperties.getExtraPermitAll() != null && !securityProperties.getExtraPermitAll().isEmpty()) {
            publicEndpoints = ArrayUtil.addAll(publicEndpoints, securityProperties.getExtraPermitAll().toArray(new String[0]));
        }
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(new TgPassthroughAuthenticationProvider()));
    }

    @Bean
    @Order(2)
    @ConditionalOnProperty(prefix = "tg.actuator", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SecurityFilterChain chain = buildJwtSecurityFilterChain(
                http, new NegatedRequestMatcher(TgActuatorSecurityConfig.actuatorRequestMatcher()));
        log.info("TgSecurityFilterChain configured (excluding /actuator/**).");
        return chain;
    }

    @Bean
    @Order(2)
    @ConditionalOnProperty(prefix = "tg.actuator", name = "enabled", havingValue = "false")
    public SecurityFilterChain securityFilterChainIncludingActuator(HttpSecurity http) throws Exception {
        return buildJwtSecurityFilterChain(http, null);
    }

    private SecurityFilterChain buildJwtSecurityFilterChain(HttpSecurity http,
                                                            NegatedRequestMatcher actuatorExclusion) throws Exception {
        if (actuatorExclusion != null) {
            http.securityMatcher(actuatorExclusion);
        }
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(publicEndpoints).permitAll()
                        .anyRequest().authenticated())
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        // 勿将 Filter 注册为 @Bean，否则 Spring Boot 会挂到全站 Servlet 链（含 /actuator/**）
        TgJwtAuthenticationFilter jwtFilter = new TgJwtAuthenticationFilter(jwtSupport, authSessionStore);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(new TgWildcardPermissionEvaluator());
        return handler;
    }
}

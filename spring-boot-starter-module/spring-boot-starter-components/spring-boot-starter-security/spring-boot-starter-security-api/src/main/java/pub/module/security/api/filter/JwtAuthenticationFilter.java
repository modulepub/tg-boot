package pub.module.security.api.filter;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.extra.spring.SpringUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import pub.module.security.api.util.JwtTokenProvider;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * JWT 认证过滤器
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {
        String requestPath = request.getServletPath();
        log.debug("requestPath:{}", requestPath);
        boolean isPublicEndpoint = isPublicEndpoint(requestPath);
        if (isPublicEndpoint) {
            filterChain.doFilter(request, response);
        } else {
            try {

                // 从请求头中获取 Token
                String token = getTokenFromRequest(request);
                // 处理认证逻辑
                if (!StringUtils.hasText(token)) {
                    throw new RuntimeException("Token is null");
                }
                // 从 Token 中获取用户名
                DecodedJWT decodedJWT = JwtTokenProvider.getDecodedJWTFromToken(token);
                Assert.notNull(decodedJWT, "Decoded JWT is null");
                String username = decodedJWT.getClaim("username").asString();
                JwtTokenProvider.validate(username);
                String password = decodedJWT.getClaim("password").asString();
                List<String> authorities = decodedJWT.getClaim("authorities").asList(String.class);
                // 加载用户信息
                AuthenticationManager authenticationManager = SpringUtil.getBean(AuthenticationManager.class);
                // 创建认证对象
                Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
                for (String authority : authorities) {

                    try {
                        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
                        grantedAuthorities.add(simpleGrantedAuthority);
                    } catch (Exception e) {
                        log.error(authority, e);
                    }
                }
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities));
                // 设置到 上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // 认证成功，继续执行过滤器链
                filterChain.doFilter(request, response);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("未授权");
                response.getWriter().flush();
            }
        }


    }

    /**
     * 检查是否为公开接口
     */
    private boolean isPublicEndpoint(String requestPath) {
        for (String pattern : pub.module.security.api.config.SecurityConfig.publicEndpoints) {
            AntPathMatcher matcher = new AntPathMatcher();
            if (matcher.match(pattern, requestPath)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 从请求中获取 Token
     * 支持从以下位置获取：
     * 1. Authorization 请求头（Bearer Token）
     * 2. token 请求参数
     *
     * @param request HTTP 请求
     * @return JWT Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. 从 Authorization 请求头获取
        String bearerToken = request.getHeader(JwtTokenProvider.jwtProperties.getHeaderName());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtTokenProvider.jwtProperties.getTokenPrefix())) {
            return bearerToken.substring(JwtTokenProvider.jwtProperties.getTokenPrefix().length()).trim();
        }
        return null;
    }
}
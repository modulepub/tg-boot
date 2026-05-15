package pub.module.system.biz.config.security.filter;

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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.biz.config.security.config.SecurityConfig;
import pub.module.system.biz.config.security.util.JwtTokenProvider;

import java.io.IOException;


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
                DecodedJWT decodedJWT = JwtTokenProvider.verify(token);
                Assert.notNull(decodedJWT, "token is null");
                // 加载用户信息
                SpringUtil.getBean(ApiSysUserService.class)
                        .authenticate(decodedJWT.getClaim("userCode").asString());
                // 认证成功，继续执行过滤器链
                filterChain.doFilter(request, response);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String msg = "Intruder, you have been locked down.Your IP address has been uploaded to the Cybersecurity Center.";
                response.getWriter().write(msg);
                response.getWriter().flush();
            }
        }


    }

    /**
     * 检查是否为公开接口
     */
    private boolean isPublicEndpoint(String requestPath) {
        for (String pattern : SecurityConfig.publicEndpoints) {
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
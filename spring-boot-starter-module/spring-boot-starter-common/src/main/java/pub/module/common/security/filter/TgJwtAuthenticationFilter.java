package pub.module.common.security.filter;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import pub.module.common.enums.ResultCodeEnum;
import pub.module.common.security.config.TgSecurityConfig;
import pub.module.common.security.jwt.JwtSupport;
import pub.module.common.security.session.AuthSessionKeys;
import pub.module.common.security.session.AuthSessionSnapshot;
import pub.module.common.security.session.AuthSessionStore;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class TgJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtSupport jwtSupport;
    private final AuthSessionStore authSessionStore;

    public TgJwtAuthenticationFilter(JwtSupport jwtSupport, AuthSessionStore authSessionStore) {
        this.jwtSupport = jwtSupport;
        this.authSessionStore = authSessionStore;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {
        String requestPath = request.getServletPath();
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            SecurityContextHolder.clearContext();
            String token = getTokenFromRequest(request);
            if (!StringUtils.hasText(token)) {
                throw new RuntimeException("Token is null");
            }
            DecodedJWT decodedJWT = jwtSupport.verify(token);
            Assert.notNull(decodedJWT, "token is null");
            String userCode = decodedJWT.getClaim("userCode").asString();
            String jti = decodedJWT.getId();
            AuthSessionSnapshot snapshot = resolveSession(userCode, jti);
            Assert.notNull(snapshot, "TOKEN 已经过期");
            Assert.isTrue(StrUtil.equals(userCode, snapshot.getUserCode()), "TOKEN 用户不匹配");
            Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
            List<String> authorities = snapshot.getAuthorities();
            if (authorities != null) {
                for (String code : authorities) {
                    if (StrUtil.isNotBlank(code)) {
                        grantedAuthorities.add(new SimpleGrantedAuthority(code.trim()));
                    }
                }
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userCode.trim(), "", grantedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String msg = StrUtil.blankToDefault(e.getMessage(), ResultCodeEnum.AUTH_FAIL.getDesc());
            //response.getWriter().write(msg);
            response.getWriter().flush();
        }
    }

    private AuthSessionSnapshot resolveSession(String userCode, String jti) {
        String normalizedUserCode = StrUtil.trim(userCode);
        String sessionKey = AuthSessionKeys.loginSessionKey(normalizedUserCode, jti);
        return authSessionStore.get(sessionKey)
                .or(() -> StrUtil.isNotBlank(jti)
                        ? authSessionStore.get(normalizedUserCode)
                        : java.util.Optional.empty())
                .orElse(null);
    }

    private boolean isPublicEndpoint(String requestPath) {
        AntPathMatcher matcher = new AntPathMatcher();
        for (String pattern : TgSecurityConfig.publicEndpoints) {
            if (matcher.match(pattern, requestPath)) {
                return true;
            }
        }
        return false;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtSupport.getProperties().getHeaderName());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtSupport.getProperties().getTokenPrefix())) {
            return bearerToken.substring(jwtSupport.getProperties().getTokenPrefix().length()).trim();
        }
        return null;
    }
}

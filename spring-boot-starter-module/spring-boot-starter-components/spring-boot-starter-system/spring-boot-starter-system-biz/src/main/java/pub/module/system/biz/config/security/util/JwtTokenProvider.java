package pub.module.system.biz.config.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT Token 提供者
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Component
public class JwtTokenProvider {

    /**
     * -- GETTER --
     * 获取 JWT 配置属性
     */
    @Getter
    public static final JwtProperties jwtProperties = new JwtProperties();
    private static final Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
    ;
    private static final JWTVerifier verifier = JWT.require(algorithm)
            .withIssuer(jwtProperties.getIssuer())
            .build();
    /**
     * 生成用户认证 Token（指定用户名）
     *
     * @param userCode 用户名
     * @return JWT Token
     */
    public static String generateToken(String userCode) {
        if (userCode == null || userCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshExpiration());
        // Constructs token with issuer, subject, and claims
        return JWT.create()
                .withIssuer(jwtProperties.getIssuer())
                .withSubject(userCode)
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withClaim("userCode", userCode)
                .sign(algorithm);
    }
    /**
     * 从 Token 中获取 Claim 值
     *
     * @param token JWT Token
     * @return Claim 值
     */
    public static DecodedJWT verify(String token) {
        // 移除 Token 前缀（如果有）
        String cleanToken = token;
        if (token.startsWith(jwtProperties.getTokenPrefix())) {
            cleanToken = token.substring(jwtProperties.getTokenPrefix().length()).trim();
        }
        return verifier.verify(cleanToken);
    }




    @Setter
    @Getter
    public static class JwtProperties {

        /**
         * JWT 密钥
         */
        private String secret = "default-secret-key-change-in-production";

        /**
         * Token 过期时间（毫秒），默认 24 小时
         */
        private Long expiration = 86400000L;

        /**
         * Token 刷新时间（毫秒），默认 7 天
         */
        private Long refreshExpiration = 604800000L;

        /**
         * 发行者
         */
        private String issuer = "tg-boot";

        /**
         * Token 前缀，默认 "Bearer "
         */
        private String tokenPrefix = "Bearer ";

        /**
         * Token 请求头名称，默认 "Authorization"
         */
        private String headerName = "Authorization";

    }
}


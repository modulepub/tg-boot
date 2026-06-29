package pub.module.common.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tg.security.jwt")
public class TgJwtProperties {

    /** 半年（按 180 天计）毫秒数 */
    public static final long HALF_YEAR_MILLIS = 180L * 24 * 60 * 60 * 1000;

    private String secret = "default-secret-key-change-in-production";

    private Long expiration = HALF_YEAR_MILLIS;

    /** 登录 Token / 会话有效期（毫秒） */
    private Long refreshExpiration = HALF_YEAR_MILLIS;

    private String issuer = "tg-boot";

    private String tokenPrefix = "Bearer ";

    private String headerName = "Authorization";
}

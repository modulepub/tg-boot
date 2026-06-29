package pub.module.common.security.jwt;

import cn.hutool.core.util.IdUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;

public class JwtSupport {

    private final TgJwtProperties properties;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtSupport(TgJwtProperties properties) {
        this.properties = properties;
        this.algorithm = Algorithm.HMAC256(properties.getSecret());
        this.verifier = JWT.require(algorithm).withIssuer(properties.getIssuer()).build();
    }

    public TgJwtProperties getProperties() {
        return properties;
    }

    public String generateToken(String userCode) {
        return generateToken(userCode, IdUtil.fastSimpleUUID());
    }

    public String generateToken(String userCode, String jti) {
        if (userCode == null || userCode.trim().isEmpty()) {
            throw new IllegalArgumentException("userCode cannot be null or empty");
        }
        if (jti == null || jti.trim().isEmpty()) {
            throw new IllegalArgumentException("jti cannot be null or empty");
        }
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + properties.getRefreshExpiration());
        return JWT.create()
                .withIssuer(properties.getIssuer())
                .withSubject(userCode)
                .withJWTId(jti.trim())
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .withClaim("userCode", userCode)
                .sign(algorithm);
    }

    public DecodedJWT verify(String token) {
        String cleanToken = token;
        if (token.startsWith(properties.getTokenPrefix())) {
            cleanToken = token.substring(properties.getTokenPrefix().length()).trim();
        }
        return verifier.verify(cleanToken);
    }
}

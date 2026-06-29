package pub.module.common.security;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;
import pub.module.common.security.config.TgActuatorSecurityConfig;
import pub.module.common.security.config.TgActuatorSecurityProperties;
import pub.module.common.security.config.TgSecurityConfig;
import pub.module.common.security.config.TgSecurityProperties;
import pub.module.common.security.jwt.JwtSupport;
import pub.module.common.security.jwt.TgJwtProperties;

@AutoConfiguration(after = pub.module.common.cache.TgEphemeralCacheAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(SecurityFilterChain.class)
@EnableConfigurationProperties({TgJwtProperties.class, TgActuatorSecurityProperties.class, TgSecurityProperties.class})
@Import({TgSecurityConfig.class, TgActuatorSecurityConfig.class})
public class TgSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtSupport jwtSupport(TgJwtProperties properties) {
        return new JwtSupport(properties);
    }
}

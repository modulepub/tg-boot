package pub.module.common.cache;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import pub.module.common.security.session.AuthSessionStore;

@AutoConfiguration
@EnableConfigurationProperties(TgEphemeralCacheProperties.class)
@ConditionalOnBean(CacheManager.class)
public class TgEphemeralCacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TgEphemeralCache tgEphemeralCache(CacheManager cacheManager, TgEphemeralCacheProperties properties) {
        return new TgEphemeralCacheImpl(cacheManager, properties.getEphemeralCacheName());
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthSessionStore authSessionStore(TgEphemeralCache tgEphemeralCache) {
        return new AuthSessionStore(tgEphemeralCache);
    }
}

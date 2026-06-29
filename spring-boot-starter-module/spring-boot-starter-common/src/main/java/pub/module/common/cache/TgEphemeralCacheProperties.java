package pub.module.common.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tg.cache")
public class TgEphemeralCacheProperties {

    /**
     * Spring Cache 区域名，全站短时 KV 共用此区域。
     */
    private String ephemeralCacheName = TgEphemeralCache.DEFAULT_CACHE_NAME;
}

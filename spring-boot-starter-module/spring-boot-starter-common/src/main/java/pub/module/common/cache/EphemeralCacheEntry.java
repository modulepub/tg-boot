package pub.module.common.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短时缓存条目（带独立过期时间，不依赖 CacheManager 全局 TTL）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EphemeralCacheEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private Serializable payload;

    private LocalDateTime expireAt;
}

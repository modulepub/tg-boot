package pub.module.common.cache;

import cn.hutool.core.util.StrUtil;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public class TgEphemeralCacheImpl implements TgEphemeralCache {

    private final CacheManager cacheManager;
    private final String cacheName;

    public TgEphemeralCacheImpl(CacheManager cacheManager, String cacheName) {
        this.cacheManager = cacheManager;
        this.cacheName = StrUtil.blankToDefault(cacheName, DEFAULT_CACHE_NAME);
    }

    @Override
    public <T extends Serializable> void put(String namespace, String key, T value, Duration ttl) {
        Cache cache = requireCache();
        cache.put(compositeKey(namespace, key), new EphemeralCacheEntry(value, LocalDateTime.now().plus(ttl)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T get(String namespace, String key, Class<T> type) {
        Cache cache = requireCache();
        Cache.ValueWrapper wrapper = cache.get(compositeKey(namespace, key));
        if (wrapper == null) {
            return null;
        }
        Object raw = wrapper.get();
        if (!(raw instanceof EphemeralCacheEntry entry)) {
            return null;
        }
        if (entry.getExpireAt() != null && entry.getExpireAt().isBefore(LocalDateTime.now())) {
            cache.evict(compositeKey(namespace, key));
            return null;
        }
        Serializable payload = entry.getPayload();
        if (payload == null) {
            return null;
        }
        if (!type.isInstance(payload)) {
            return null;
        }
        return (T) payload;
    }

    @Override
    public void evict(String namespace, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(compositeKey(namespace, key));
        }
    }

    private Cache requireCache() {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException("Cache '" + cacheName + "' not configured. Enable spring.cache and Redis/Caffeine.");
        }
        return cache;
    }

    static String compositeKey(String namespace, String key) {
        return StrUtil.trim(namespace) + "::" + StrUtil.trim(key);
    }
}

package pub.module.common.cache;

import java.io.Serializable;
import java.time.Duration;

/**
 * 通用短时 KV 缓存（无表、单 Cache 区域 + namespace 隔离）。
 * 业务域使用自有 namespace，例如 {@code sys:sms}、{@code auth:session}。
 */
public interface TgEphemeralCache {

    String DEFAULT_CACHE_NAME = "tgEphemeral";

    <T extends Serializable> void put(String namespace, String key, T value, Duration ttl);

    <T extends Serializable> T get(String namespace, String key, Class<T> type);

    void evict(String namespace, String key);
}

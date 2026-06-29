package pub.module.common.security.session;

import pub.module.common.cache.TgEphemeralCache;

import java.time.Duration;
import java.util.Optional;

/**
 * 认证会话存储，固定 namespace {@link #NAMESPACE}。
 */
public class AuthSessionStore {

    public static final String NAMESPACE = "auth:session";

    private final TgEphemeralCache cache;

    public AuthSessionStore(TgEphemeralCache cache) {
        this.cache = cache;
    }

    public void put(String sessionKey, AuthSessionSnapshot snapshot, Duration ttl) {
        cache.put(NAMESPACE, sessionKey, snapshot, ttl);
    }

    public Optional<AuthSessionSnapshot> get(String sessionKey) {
        return Optional.ofNullable(cache.get(NAMESPACE, sessionKey, AuthSessionSnapshot.class));
    }

    public void remove(String sessionKey) {
        cache.evict(NAMESPACE, sessionKey);
    }
}

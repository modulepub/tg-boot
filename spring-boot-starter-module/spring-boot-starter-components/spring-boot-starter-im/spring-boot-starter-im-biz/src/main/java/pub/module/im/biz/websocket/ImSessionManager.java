package pub.module.im.biz.websocket;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IM 多端会话管理器
 * 管理用户与 WebSocket Session 的映射关系，支持同一用户多端登录
 */
@Component
public class ImSessionManager {

    // userCode -> Set<sessionId>
    private final ConcurrentHashMap<String, Set<String>> userSessions = new ConcurrentHashMap<>();
    // sessionId -> userCode
    private final ConcurrentHashMap<String, String> sessionUsers = new ConcurrentHashMap<>();

    public void register(String userCode, String sessionId) {
        userSessions.computeIfAbsent(userCode, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        sessionUsers.put(sessionId, userCode);
    }

    public void unregister(String sessionId) {
        String userCode = sessionUsers.remove(sessionId);
        if (userCode != null) {
            Set<String> sessions = userSessions.get(userCode);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    userSessions.remove(userCode);
                }
            }
        }
    }

    public boolean isOnline(String userCode) {
        Set<String> sessions = userSessions.get(userCode);
        return sessions != null && !sessions.isEmpty();
    }

    public Set<String> getUserSessions(String userCode) {
        return userSessions.getOrDefault(userCode, Collections.emptySet());
    }

    public String getUserCodeBySession(String sessionId) {
        return sessionUsers.get(sessionId);
    }

    public int getOnlineUserCount() {
        return userSessions.size();
    }
}

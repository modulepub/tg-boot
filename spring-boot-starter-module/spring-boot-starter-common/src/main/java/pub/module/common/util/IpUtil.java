package pub.module.common.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {
    /**
     */
    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public static String getRealIp(HttpServletRequest request) {
        // 1. 优先解析 X-Forwarded-For（多级代理时，第一个IP是客户端真实IP）
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 2. 解析 X-Real-IP（单级代理常用）
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 3. 兜底：获取代理服务器IP（无代理时为客户端IP）
            ip = request.getRemoteAddr();
        }

        // 处理多级代理：X-Forwarded-For可能是 "客户端IP, 代理IP1, 代理IP2"，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // 本地回环地址转换
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip == null ? "unknown" : ip;
    }
}

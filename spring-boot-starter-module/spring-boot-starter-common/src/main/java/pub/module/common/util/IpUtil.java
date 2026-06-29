package pub.module.common.util;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析客户端真实 IP，兼容 CDN / 反向代理回源头。
 * <p>
 * 腾讯云 CDN 默认通过 {@code X-Forwarded-For} 回传客户端 IP（取最左侧公网 IP）；
 * 阿里云 CDN 默认透传 {@code Ali-Cdn-Real-Ip}。
 * </p>
 */
public final class IpUtil {

    private static final Pattern FORWARDED_FOR_PATTERN = Pattern.compile("for=([^;\\s]+)", Pattern.CASE_INSENSITIVE);

    /**
     * 回源 HTTP 头优先级：腾讯云 CDN 以 X-Forwarded-For 为主，阿里云 CDN 以 Ali-Cdn-Real-Ip 为主。
     */
    private static final String[] CLIENT_IP_HEADERS = {
            "X-Forwarded-For",
            "X-Client-IP",
            "Cdn-Src-Ip",
            "True-Client-Ip",
            "CF-Connecting-IP",
            "Ali-Cdn-Real-Ip",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
    };

    private IpUtil() {
    }

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public static String getRealIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        return getRealIp(attributes.getRequest());
    }

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public static String getRealIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        for (String headerName : CLIENT_IP_HEADERS) {
            String raw = request.getHeader(headerName);
            String candidate = "X-Forwarded-For".equalsIgnoreCase(headerName)
                    ? firstPublicIp(raw)
                    : firstValidIp(raw);
            if (StrUtil.isNotBlank(candidate)) {
                return candidate;
            }
        }

        String forwarded = firstIpFromForwardedHeader(request.getHeader("Forwarded"));
        if (StrUtil.isNotBlank(forwarded)) {
            return forwarded;
        }

        String remoteAddr = normalizeIp(request.getRemoteAddr());
        return StrUtil.blankToDefault(remoteAddr, "unknown");
    }

    /**
     * 从 X-Forwarded-For 中取最左侧公网 IP（腾讯云 CDN 重置后通常仅一项；多级代理时跳过内网/CDN 节点）。
     */
    private static String firstPublicIp(String raw) {
        if (StrUtil.isBlank(raw) || "unknown".equalsIgnoreCase(raw.trim())) {
            return null;
        }
        String fallback = null;
        for (String part : raw.split(",")) {
            String ip = normalizeIp(part);
            if (!isUsableIp(ip)) {
                continue;
            }
            if (fallback == null) {
                fallback = ip;
            }
            if (!NetUtil.isInnerIP(ip)) {
                return ip;
            }
        }
        return fallback;
    }

    private static String firstValidIp(String raw) {
        if (StrUtil.isBlank(raw) || "unknown".equalsIgnoreCase(raw.trim())) {
            return null;
        }
        for (String part : raw.split(",")) {
            String ip = normalizeIp(part);
            if (isUsableIp(ip)) {
                return ip;
            }
        }
        return null;
    }

    private static String firstIpFromForwardedHeader(String raw) {
        if (StrUtil.isBlank(raw)) {
            return null;
        }
        Matcher matcher = FORWARDED_FOR_PATTERN.matcher(raw);
        while (matcher.find()) {
            String ip = normalizeIp(matcher.group(1));
            if (StrUtil.isNotBlank(ip) && isUsableIp(ip) && !NetUtil.isInnerIP(ip)) {
                return ip;
            }
        }
        return null;
    }

    private static String normalizeIp(String raw) {
        String ip = StrUtil.trimToNull(raw);
        if (ip == null) {
            return null;
        }
        if (ip.startsWith("\"") && ip.endsWith("\"") && ip.length() > 1) {
            ip = ip.substring(1, ip.length() - 1);
        }
        if (ip.startsWith("for=")) {
            ip = ip.substring(4);
        }
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        if (ip.startsWith("[") && ip.endsWith("]")) {
            return ip.substring(1, ip.length() - 1);
        }
        return ip;
    }

    private static boolean isUsableIp(String ip) {
        return StrUtil.isNotBlank(ip)
                && !"unknown".equalsIgnoreCase(ip)
                && (Validator.isIpv4(ip) || Validator.isIpv6(ip));
    }
}

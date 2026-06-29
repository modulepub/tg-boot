package pub.module.common.util;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * IP 归属地解析（内网 IP 直接返回固定文案，公网 IP 调用公开接口）。
 */
@Slf4j
public final class IpLocationUtil {

    private IpLocationUtil() {
    }

    public static String resolve(String ip) {
        String normalized = StrUtil.trimToEmpty(ip);
        if (StrUtil.isBlank(normalized) || "unknown".equalsIgnoreCase(normalized)) {
            return "";
        }
        if ("127.0.0.1".equals(normalized) || NetUtil.isInnerIP(normalized)) {
            return "内网IP";
        }
        try {
            String body = HttpUtil.get("https://whois.pconline.com.cn/ipJson.jsp?ip="
                    + normalized + "&json=true", 2000);
            if (StrUtil.isBlank(body)) {
                return "";
            }
            JSONObject json = JSONUtil.parseObj(body);
            String addr = StrUtil.trim(json.getStr("addr"));
            if (StrUtil.isNotBlank(addr)) {
                return addr;
            }
            String pro = StrUtil.trim(json.getStr("pro"));
            String city = StrUtil.trim(json.getStr("city"));
            return StrUtil.trim(pro + " " + city);
        }
        catch (Exception e) {
            log.debug("resolve ip location failed, ip={}: {}", normalized, e.getMessage());
            return "";
        }
    }
}

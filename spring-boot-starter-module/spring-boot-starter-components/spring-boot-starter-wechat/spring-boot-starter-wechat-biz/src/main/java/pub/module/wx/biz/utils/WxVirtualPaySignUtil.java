package pub.module.wx.biz.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * 微信小程序虚拟支付签名工具。
 */
public final class WxVirtualPaySignUtil {

    public static final String REQUEST_VIRTUAL_PAYMENT_URI = "requestVirtualPayment";
    public static final String QUERY_ORDER_URI = "/xpay/query_order";

    private WxVirtualPaySignUtil() {
    }

    public static String hmacSha256Hex(String key, String message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(raw);
        }
        catch (Exception e) {
            throw new IllegalStateException("虚拟支付签名失败", e);
        }
    }

    public static String paySig(String appKey, String uri, String body) {
        return hmacSha256Hex(appKey, uri + "&" + body);
    }

    public static String userSignature(String sessionKey, String signData) {
        return hmacSha256Hex(sessionKey, signData);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

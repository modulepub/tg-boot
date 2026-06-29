package pub.module.wx.biz.utils;

/**
 * 构造微信虚拟支付 signData 字符串（字段顺序与微信文档一致，goodsPrice 为数字分）。
 */
public final class WxVirtualPaySignDataUtil {

    private WxVirtualPaySignDataUtil() {
    }

    public static String buildSignData(String offerId,
                                       int buyQuantity,
                                       int env,
                                       String productId,
                                       int goodsPriceFen,
                                       String outTradeNo,
                                       String attach) {
        return "{"
                + "\"offerId\":\"" + escapeJson(offerId) + "\","
                + "\"buyQuantity\":" + buyQuantity + ","
                + "\"env\":" + env + ","
                + "\"currencyType\":\"CNY\","
                + "\"productId\":\"" + escapeJson(productId) + "\","
                + "\"goodsPrice\":" + goodsPriceFen + ","
                + "\"outTradeNo\":\"" + escapeJson(outTradeNo) + "\","
                + "\"attach\":\"" + escapeJson(attach) + "\""
                + "}";
    }

    private static String escapeJson(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}

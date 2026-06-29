package pub.module.wx.biz.utils;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.wx.biz.config.WxMaRuntimeRefresher;
import pub.module.wx.biz.config.WxVirtualPayRuntimeConfig;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 微信虚拟支付服务端 API 客户端（查单等）。
 */
@Slf4j
@Component
public class WxVirtualPayClient {

    private static final String QUERY_ORDER_URL = "https://api.weixin.qq.com/xpay/query_order";

    @Resource
    private WxMaService wxMaService;
    @Resource
    private WxMaRuntimeRefresher wxMaRuntimeRefresher;

    /**
     * 查询虚拟支付订单是否已支付。
     *
     * @param orderId 业务订单号（signData.outTradeNo，对应 query_order 的 order_id）
     */
    public boolean isOrderPaid(WxVirtualPayRuntimeConfig cfg, String openId, String orderId) {
        if (cfg == null || StrUtil.hasBlank(openId, orderId)) {
            return false;
        }
        try {
            wxMaRuntimeRefresher.ensureLoaded();
            wxMaService.switchoverTo(cfg.getAppId());
            String accessToken = wxMaService.getAccessToken();

            // 字段顺序须与微信文档一致：openid → env → order_id
            Map<String, Object> bodyMap = new LinkedHashMap<>();
            bodyMap.put("openid", openId.trim());
            bodyMap.put("env", cfg.resolveEnv());
            bodyMap.put("order_id", orderId.trim());
            String postBody = JSONUtil.toJsonStr(bodyMap);

            String paySig = WxVirtualPaySignUtil.paySig(
                    cfg.resolveAppKey(),
                    WxVirtualPaySignUtil.QUERY_ORDER_URI,
                    postBody);
            String url = QUERY_ORDER_URL + "?access_token=" + accessToken + "&pay_sig=" + paySig;
            String resp = HttpUtil.post(url, postBody);
            log.info("xpay query_order resp: {}", resp);
            if (!JSONUtil.isTypeJSON(resp)) {
                return false;
            }
            JSONObject json = JSONUtil.parseObj(resp);
            int errcode = json.getInt("errcode", -1);
            if (errcode != 0) {
                log.warn("xpay query_order errcode={}, errmsg={}, orderId={}",
                        errcode, json.getStr("errmsg"), orderId);
                return false;
            }
            JSONObject order = json.getJSONObject("order");
            if (order == null) {
                return false;
            }
            Integer status = order.getInt("status");
            // 2-已支付待发货 3-发货中 4-已发货
            return status != null && status >= 2 && status <= 4;
        }
        catch (Exception e) {
            log.warn("xpay query_order failed, orderId={}, err={}", orderId, e.getMessage());
            return false;
        }
    }
}

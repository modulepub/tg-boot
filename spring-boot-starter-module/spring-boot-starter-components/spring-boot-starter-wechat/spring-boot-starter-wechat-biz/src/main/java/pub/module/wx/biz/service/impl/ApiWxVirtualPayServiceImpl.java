package pub.module.wx.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.wx.api.service.ApiWxMaSessionService;
import pub.module.wx.api.service.ApiWxVirtualPayService;
import pub.module.wx.biz.config.WxVirtualPayRuntimeConfig;
import pub.module.wx.biz.config.WxVirtualPayRuntimeRefresher;
import pub.module.wx.biz.utils.WxVirtualPayClient;
import pub.module.wx.biz.utils.WxVirtualPaySignDataUtil;
import pub.module.wx.biz.utils.WxVirtualPaySignUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 微信小程序虚拟支付业务实现。
 */
@Slf4j
@Service
public class ApiWxVirtualPayServiceImpl implements ApiWxVirtualPayService {

    private static final String MODE_SHORT_SERIES_GOODS = "short_series_goods";

    @Resource
    private WxVirtualPayRuntimeRefresher wxVirtualPayRuntimeRefresher;
    @Resource
    private ApiWxMaSessionService apiWxMaSessionService;
    @Resource
    private WxVirtualPayClient wxVirtualPayClient;

    @Override
    public VirtualPayResultDTO createPayment(VirtualPayReqDTO req) {
        Assert.notNull(req, "虚拟支付请求不能为空");
        Assert.notBlank(req.getAppId(), "appId 不能为空");
        Assert.notBlank(req.getCode(), "code 不能为空");
        Assert.notBlank(req.getTradeNo(), "tradeNo 不能为空");
        Assert.notBlank(req.getProductId(), "productId 不能为空");
        Assert.notNull(req.getAmount(), "amount 不能为空");

        WxVirtualPayRuntimeConfig cfg = wxVirtualPayRuntimeRefresher.requireByAppId(req.getAppId());
        ApiWxMaSessionService.MaSessionDTO session = apiWxMaSessionService.getSessionByCode(req.getAppId(), req.getCode());
        Assert.notBlank(session.getSessionKey(), "sessionKey 不能为空");

        int env = cfg.resolveEnv();
        int buyQuantity = req.getBuyQuantity() == null || req.getBuyQuantity() <= 0 ? 1 : req.getBuyQuantity();
        int goodsPriceFen = req.getAmount()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
        Assert.isTrue(goodsPriceFen > 0, "商品单价必须大于 0");

        String attach = StrUtil.blankToDefault(req.getAttach(), req.getTradeNo());
        String signData = WxVirtualPaySignDataUtil.buildSignData(
                cfg.getOfferId(), buyQuantity, env, req.getProductId(), goodsPriceFen, req.getTradeNo(), attach);
        String appKey = cfg.resolveAppKey();
        String paySig = WxVirtualPaySignUtil.paySig(appKey, WxVirtualPaySignUtil.REQUEST_VIRTUAL_PAYMENT_URI, signData);
        String signature = WxVirtualPaySignUtil.userSignature(session.getSessionKey(), signData);

        VirtualPayResultDTO result = new VirtualPayResultDTO();
        result.setSignData(signData);
        result.setPaySig(paySig);
        result.setSignature(signature);
        result.setMode(MODE_SHORT_SERIES_GOODS);
        log.info("virtual pay prepared, env={}, tradeNo={}, productId={}, goodsPriceFen={}, signData={}",
                env, req.getTradeNo(), req.getProductId(), goodsPriceFen, signData);
        return result;
    }

    @Override
    public VirtualPayQueryResultDTO queryOrder(VirtualPayQueryReqDTO req) {
        VirtualPayQueryResultDTO result = new VirtualPayQueryResultDTO();
        result.setPaidSuccess(false);
        if (req == null || StrUtil.hasBlank(req.getAppId(), req.getTradeNo())) {
            return result;
        }
        result.setTradeNo(req.getTradeNo());
        WxVirtualPayRuntimeConfig cfg = wxVirtualPayRuntimeRefresher.requireByAppId(req.getAppId());
        String openId = null;
        if (StrUtil.isNotBlank(req.getCode())) {
            openId = apiWxMaSessionService.getOpenIdByCode(req.getAppId(), req.getCode());
        }
        if (StrUtil.isBlank(openId)) {
            return result;
        }
        boolean paid = queryOrderPaidWithRetry(cfg, openId, req.getTradeNo());
        result.setPaidSuccess(paid);
        return result;
    }

    private boolean queryOrderPaidWithRetry(WxVirtualPayRuntimeConfig cfg, String openId, String orderId) {
        int[] delaysMs = {0, 800, 1500};
        for (int delayMs : delaysMs) {
            if (delayMs > 0) {
                try {
                    Thread.sleep(delayMs);
                }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            if (wxVirtualPayClient.isOrderPaid(cfg, openId, orderId)) {
                return true;
            }
        }
        return false;
    }
}

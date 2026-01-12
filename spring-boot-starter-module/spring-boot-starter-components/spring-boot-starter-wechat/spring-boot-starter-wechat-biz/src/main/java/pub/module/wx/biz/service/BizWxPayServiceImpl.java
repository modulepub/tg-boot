package pub.module.wx.biz.service;

import cn.hutool.core.bean.BeanUtil;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.wx.api.service.BizWxPayService;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * 微信支付业务服务实现类
 * 提供微信支付相关的业务功能，包括创建订单和查询订单
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Service
@Slf4j
public class BizWxPayServiceImpl implements BizWxPayService {
    @Resource
    WxPayService wxPayService;


    @SneakyThrows
    @Override
    public JsapiResultDTO createOrderV3(WxPayRepDTO wxPayRepDTO) {
        wxPayService.switchover(wxPayRepDTO.getAppId());
        ArrayList<WxPayUnifiedOrderV3Request.GoodsDetail> goodsDetails = new ArrayList<>();
        // 构建支付参数
        WxPayUnifiedOrderV3Request v3Request = new WxPayUnifiedOrderV3Request();
        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setTotal(new BigDecimal(wxPayRepDTO.getAmount()).multiply(new BigDecimal(100)).intValue());
        v3Request.setAmount(amount);
        //调起支付的人的 openId
        goodsDetails.add(new WxPayUnifiedOrderV3Request.GoodsDetail() {
        }
                .setMerchantGoodsId("")
                .setUnitPrice(11).setQuantity(22));

        WxPayConfig wxPayConfig = wxPayService.getConfig();
        v3Request.setAppid(wxPayConfig.getAppId());
        v3Request.setMchid(wxPayConfig.getMchId());
        v3Request.setNotifyUrl("https://api.iqingqing.net/tg/pub/wechat/parseOrderNotifyResult");
        v3Request.setDescription(wxPayRepDTO.getRemark());
        v3Request.setOutTradeNo(wxPayRepDTO.getTradeNo());
        WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer();
        payer.setOpenid(wxPayRepDTO.getOpenId());
        v3Request.setPayer(payer);
        v3Request.setDetail(new WxPayUnifiedOrderV3Request.Discount() {
        }.setInvoiceId("").setGoodsDetails(goodsDetails));
        v3Request.setSceneInfo(new WxPayUnifiedOrderV3Request.SceneInfo() {
        }
                .setStoreInfo(new WxPayUnifiedOrderV3Request.StoreInfo() {
                }.setId("")).setPayerClientIp(""));

        WxPayUnifiedOrderV3Result.JsapiResult jsapiResult = wxPayService.createOrderV3(TradeTypeEnum.valueOf("JSAPI"), v3Request);
        JsapiResultDTO result = BeanUtil.copyProperties(jsapiResult, JsapiResultDTO.class);
        log.info("wx pay result:{}", result);
        return result;
    }

    @Override
    public WxPayOrderQueryV3Result queryOrderV3(WxQueryPayReqDTO wxQueryPayReqDTO) {
        WxPayOrderQueryV3Result result = new WxPayOrderQueryV3Result();
        result.setPaidSuccess(false);
        com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result v3Result;
        try {
            v3Result = this.wxPayService.queryOrderV3(wxQueryPayReqDTO.getTransactionId(), wxQueryPayReqDTO.getTradeNo());
            result.setTradeNo(v3Result.getOutTradeNo());
            result.setAmount(new BigDecimal(v3Result.getAmount().getTotal()).divide(new BigDecimal("10"), 2, RoundingMode.HALF_UP));
        } catch (WxPayException e) {
            log.error("查询微信后台订单失败：{}", e.getMessage());
        }
        return result;
    }
}

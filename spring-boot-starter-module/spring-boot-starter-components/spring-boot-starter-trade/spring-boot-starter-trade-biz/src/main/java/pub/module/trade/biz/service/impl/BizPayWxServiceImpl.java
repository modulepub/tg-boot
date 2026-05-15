package pub.module.trade.biz.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.trade.api.constants.TdOdPaidStatusCodeEnum;
import pub.module.trade.biz.service.BizPayService;
import pub.module.trade.biz.service.BizTradeOrderService;
import pub.module.trade.curd.entity.TdOrder;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service("wx")
@Slf4j
public class BizPayWxServiceImpl implements BizPayService {
    @Resource
    WxPayService wxPayService;
    @Resource
    BizTradeOrderService bizTradeOrderService;

    @Transactional
    @Override
    public TdOrder getPayResult(PayResultReq req) {
        TdOrder tdOrder = bizTradeOrderService.queryOrderByCode(req.getTdOdCode());
        if (!TdOdPaidStatusCodeEnum.PAID.getCode().equals(tdOrder.getTdOdPaidStatusCode())) {
            String appId = req.getPlatParam().getStr("appId");
            String transactionId = req.getPlatParam().getStr("transactionId");
            String tradeNo = req.getPlatParam().getStr("transactionId");
            wxPayService.switchover(appId);
            try {
                WxPayOrderQueryV3Result v3Result = this.wxPayService.queryOrderV3(transactionId, tradeNo);
                if (v3Result.getTradeState().equals("SUCCESS")) {
                    bizTradeOrderService.paidOrder(tradeNo);
                }
            } catch (WxPayException e) {
                throw new RuntimeException(e);
            }
        }
        return tdOrder;
    }

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrePayDTO.Res prePay(PrePayDTO.Req req) {
        PrePayDTO.Res result = new PrePayDTO.Res();
        String appId = req.getPlatParam().getStr("appId");
        String remark = req.getPlatParam().getStr("remark");
        String tradeNo = req.getPlatParam().getStr("tradeNo");
        String openId = req.getPlatParam().getStr("openId");
        wxPayService.switchover(appId);
        ArrayList<WxPayUnifiedOrderV3Request.GoodsDetail> goodsDetails = new ArrayList<>();
        WxPayUnifiedOrderV3Request v3Request = new WxPayUnifiedOrderV3Request();
        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setTotal(req.getTdOdAmount().multiply(new BigDecimal(100)).intValue());
        v3Request.setAmount(amount);
        //调起支付的人的 openId
        goodsDetails.add(new WxPayUnifiedOrderV3Request.GoodsDetail() {
        }
                .setMerchantGoodsId("")
                .setUnitPrice(11).setQuantity(22));

        WxPayConfig wxPayConfig = wxPayService.getConfig();
        v3Request.setAppid(wxPayConfig.getAppId());
        v3Request.setMchid(wxPayConfig.getMchId());
        v3Request.setNotifyUrl(wxPayConfig.getNotifyUrl());
        v3Request.setDescription(remark);
        v3Request.setOutTradeNo(tradeNo);
        WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer();
        payer.setOpenid(openId);
        v3Request.setPayer(payer);
        v3Request.setDetail(new WxPayUnifiedOrderV3Request.Discount() {
        }.setInvoiceId("").setGoodsDetails(goodsDetails));
        v3Request.setSceneInfo(new WxPayUnifiedOrderV3Request.SceneInfo() {
        }
                .setStoreInfo(new WxPayUnifiedOrderV3Request.StoreInfo() {
                }.setId("")).setPayerClientIp(""));

        WxPayUnifiedOrderV3Result.JsapiResult jsapiResult = wxPayService.createOrderV3(TradeTypeEnum.valueOf("JSAPI"), v3Request);
        result.setPlatParam(JSONUtil.parseObj(JSONUtil.toJsonStr(jsapiResult)));
        log.info("wx pay result:{}", result);
        return result;
    }

}

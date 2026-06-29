package pub.module.wx.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.wx.api.service.ApiWxPayService;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 微信支付业务实现（统一下单、查单）。
 */
@Service
@Slf4j
public class ApiWxPayServiceImpl implements ApiWxPayService {

    @Resource
    private WxPayService wxPayService;

    @SneakyThrows
    @Override
    public JsapiResultDTO createOrderV3(WxPayRepDTO wxPayRepDTO) {
        wxPayService.switchover(wxPayRepDTO.getAppId());
        WxPayUnifiedOrderV3Request v3Request = new WxPayUnifiedOrderV3Request();
        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setTotal(new BigDecimal(wxPayRepDTO.getAmount()).multiply(new BigDecimal(100)).intValue());
        v3Request.setAmount(amount);

        com.github.binarywang.wxpay.config.WxPayConfig wxPayConfig = wxPayService.getConfig();
        v3Request.setAppid(wxPayConfig.getAppId());
        v3Request.setMchid(wxPayConfig.getMchId());
        v3Request.setNotifyUrl(wxPayConfig.getNotifyUrl());
        v3Request.setDescription(wxPayRepDTO.getRemark());
        v3Request.setOutTradeNo(wxPayRepDTO.getTradeNo());
        WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer();
        payer.setOpenid(wxPayRepDTO.getOpenId());
        v3Request.setPayer(payer);

        WxPayUnifiedOrderV3Result.JsapiResult jsapiResult =
                wxPayService.createOrderV3(TradeTypeEnum.JSAPI, v3Request);
        JsapiResultDTO result = BeanUtil.copyProperties(jsapiResult, JsapiResultDTO.class);
        log.info("wx pay result:{}", result);
        return result;
    }

    @Override
    public WxPayOrderQueryV3Result queryOrderV3(WxQueryPayReqDTO wxQueryPayReqDTO) {
        WxPayOrderQueryV3Result result = new WxPayOrderQueryV3Result();
        result.setPaidSuccess(false);
        if (StrUtil.isNotBlank(wxQueryPayReqDTO.getAppId())) {
            wxPayService.switchover(wxQueryPayReqDTO.getAppId());
        }
        try {
            com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result v3Result =
                    wxPayService.queryOrderV3(wxQueryPayReqDTO.getTransactionId(), wxQueryPayReqDTO.getTradeNo());
            result.setTradeNo(v3Result.getOutTradeNo());
            if (v3Result.getAmount() != null && v3Result.getAmount().getTotal() != null) {
                result.setAmount(new BigDecimal(v3Result.getAmount().getTotal())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            }
            result.setPaidSuccess("SUCCESS".equals(v3Result.getTradeState()));
        }
        catch (WxPayException e) {
            log.error("查询微信后台订单失败：{}", e.getMessage());
        }
        return result;
    }
}

package pub.module.trade.biz.service.impl;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.trade.api.constants.TradePaidChannelEnum;
import pub.module.trade.biz.service.BizPayService;
import pub.module.trade.biz.service.BizTradeOrderService;
import pub.module.trade.crud.entity.TdOrder;
import pub.module.wx.api.service.ApiWxPayService;

/**
 * 交易模块微信支付渠道适配器（委托 wechat-api 的 {@link ApiWxPayService}）。
 */
@Service
@Slf4j
public class BizPayWxServiceImpl implements BizPayService {

    @Resource
    private ApiWxPayService apiWxPayService;
    @Resource
    private BizTradeOrderService bizTradeOrderService;

    @Override
    public String paidChannelCode() {
        return TradePaidChannelEnum.WX.getCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrePayDTO.Res prePay(PrePayDTO.Req req) {
        ApiWxPayService.WxPayRepDTO dto = new ApiWxPayService.WxPayRepDTO();
        dto.setAppId(req.getPlatParam().getStr("appId"));
        dto.setRemark(req.getPlatParam().getStr("remark"));
        dto.setTradeNo(req.getPlatParam().getStr("tradeNo"));
        dto.setOpenId(req.getPlatParam().getStr("openId"));
        dto.setAmount(req.getTdOdAmount().toPlainString());

        ApiWxPayService.JsapiResultDTO jsapiResult = apiWxPayService.createOrderV3(dto);
        PrePayDTO.Res result = new PrePayDTO.Res();
        result.setPlatParam(JSONUtil.parseObj(JSONUtil.toJsonStr(jsapiResult)));
        log.info("wx pay result:{}", result);
        return result;
    }

    @Transactional
    @Override
    public TdOrder getPayResult(PayResultReq req) {
        TdOrder tdOrder = bizTradeOrderService.queryOrderByCode(req.getTdOdCode());
        if (!StatusCodeEnum.YES.getCode().equals(tdOrder.getTdOdPaidStatusCode())) {
            ApiWxPayService.WxQueryPayReqDTO queryDto = new ApiWxPayService.WxQueryPayReqDTO();
            queryDto.setAppId(req.getPlatParam().getStr("appId"));
            queryDto.setTransactionId(req.getPlatParam().getStr("transactionId"));
            queryDto.setTradeNo(req.getPlatParam().getStr("transactionId"));
            ApiWxPayService.WxPayOrderQueryV3Result queryResult = apiWxPayService.queryOrderV3(queryDto);
            if (Boolean.TRUE.equals(queryResult.getPaidSuccess())) {
                bizTradeOrderService.paidOrder(queryResult.getTradeNo());
            }
        }
        return tdOrder;
    }
}

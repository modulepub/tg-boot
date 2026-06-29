package pub.module.trade.biz.service.impl;

import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.trade.api.constants.TradePaidChannelEnum;
import pub.module.trade.biz.service.BizPayService;
import pub.module.trade.biz.service.BizTradeOrderService;
import pub.module.trade.crud.entity.TdOrder;
import pub.module.wx.api.service.ApiWxVirtualPayService;

/**
 * 交易模块微信虚拟支付渠道适配器（委托 wechat-api 的 {@link ApiWxVirtualPayService}）。
 */
@Service
@Slf4j
public class BizPayWxVirtualServiceImpl implements BizPayService {

    @Resource
    private ApiWxVirtualPayService apiWxVirtualPayService;
    @Resource
    private BizTradeOrderService bizTradeOrderService;

    @Override
    public String paidChannelCode() {
        return TradePaidChannelEnum.WX_VIRTUAL.getCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrePayDTO.Res prePay(PrePayDTO.Req req) {
        ApiWxVirtualPayService.VirtualPayReqDTO dto = new ApiWxVirtualPayService.VirtualPayReqDTO();
        dto.setAppId(req.getPlatParam().getStr("appId"));
        dto.setCode(req.getPlatParam().getStr("code"));
        dto.setTradeNo(req.getPlatParam().getStr("tradeNo"));
        dto.setProductId(req.getPlatParam().getStr("productId"));
        dto.setAmount(req.getTdOdAmount());
        dto.setBuyQuantity(req.getPlatParam().getInt("buyQuantity", 1));
        dto.setAttach(req.getPlatParam().getStr("attach"));

        ApiWxVirtualPayService.VirtualPayResultDTO virtualPayResult = apiWxVirtualPayService.createPayment(dto);
        PrePayDTO.Res result = new PrePayDTO.Res();
        // signData 必须保持为原始 JSON 字符串，避免 JSON 往返把内层 JSON 解析成对象导致 paySig 失效
        JSONObject platParam = new JSONObject(true);
        platParam.set("signData", virtualPayResult.getSignData());
        platParam.set("paySig", virtualPayResult.getPaySig());
        platParam.set("signature", virtualPayResult.getSignature());
        platParam.set("mode", virtualPayResult.getMode());
        result.setPlatParam(platParam);
        log.info("wx virtual pay result platParam.signData={}", virtualPayResult.getSignData());
        return result;
    }

    @Transactional
    @Override
    public TdOrder getPayResult(PayResultReq req) {
        TdOrder tdOrder = bizTradeOrderService.queryOrderByCode(req.getTdOdCode());
        if (!StatusCodeEnum.YES.getCode().equals(tdOrder.getTdOdPaidStatusCode())) {
            ApiWxVirtualPayService.VirtualPayQueryReqDTO queryDto = new ApiWxVirtualPayService.VirtualPayQueryReqDTO();
            queryDto.setAppId(req.getPlatParam().getStr("appId"));
            queryDto.setCode(req.getPlatParam().getStr("code"));
            queryDto.setTradeNo(req.getTdOdCode());
            ApiWxVirtualPayService.VirtualPayQueryResultDTO queryResult = apiWxVirtualPayService.queryOrder(queryDto);
            if (Boolean.TRUE.equals(queryResult.getPaidSuccess())) {
                bizTradeOrderService.paidOrder(queryResult.getTradeNo());
                tdOrder = bizTradeOrderService.queryOrderByCode(req.getTdOdCode());
            }
        }
        return tdOrder;
    }
}

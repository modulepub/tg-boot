package pub.module.finance.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.finance.api.constants.*;
import pub.module.finance.api.service.BizFcAccountLogService;
import pub.module.finance.api.service.BizPayService;

import jakarta.annotation.Resource;

/**
 * 支付
 */
@Slf4j
@Service(FcProductCodeEnum.ZY_SL_CODE)
public class BizPayShoppingLoanServiceImpl implements BizPayService {
    @Resource
    BizFcAccountLogService bizFcAccountLogService;




    /**
     * 1、信用账户：生成对应预支付记录->返回支付流水号-回调
     * 2、普通账户：生成对应预支付记录 -->返回支付流水号->回调
     * 3、支付宝、银行卡、微信：生成对应预支付记录-->返回支付流水号-回调
     */
    @Transactional
    public PrePayDTO.Res prePay(PrePayDTO.Req prePayDTO) {
        PrePayDTO.Res result = new PrePayDTO.Res();
        bizFcAccountLogService.savePayReqLog(prePayDTO);
        return result;
    }

    @Override
    public QueryPayResultDTO.Res getPayResult(QueryPayResultDTO.Req req) {
        return null;
    }


}

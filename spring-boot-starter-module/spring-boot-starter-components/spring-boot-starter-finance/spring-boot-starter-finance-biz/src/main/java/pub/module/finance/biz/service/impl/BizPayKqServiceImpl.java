package pub.module.finance.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.finance.api.constants.FcProductCodeEnum;
import pub.module.finance.api.service.BizFcAccountLogService;
import pub.module.finance.api.service.BizFcAccountService;
import pub.module.finance.api.service.BizPayService;
import pub.module.finance.curd.entity.FcAccount;
import pub.module.finance.curd.entity.FcAccountLog;

import jakarta.annotation.Resource;

/**
 * 支付
 */
@Slf4j
@Service(FcProductCodeEnum.KQ_STR)
public class BizPayKqServiceImpl implements BizPayService {
    @Resource
    BizFcAccountLogService bizFcAccountLogService;
    @Resource
    BizFcAccountService bizFcAccountService;




    /**
     * 1、信用账户：生成对应预支付记录->返回支付流水号-回调
     * 2、普通账户：生成对应预支付记录 -->返回支付流水号->回调
     * 3、支付宝、银行卡、微信：生成对应预支付记录-->返回支付流水号-回调
     */
    @Transactional
    public PrePayDTO.Res prePay(PrePayDTO.Req prePayDTO) {
        PrePayDTO.Res  result = new PrePayDTO.Res();
        return result;
    }

    @Override
    public QueryPayResultDTO.Res getPayResult(QueryPayResultDTO.Req req) {
        return null;
    }


}

package pub.module.finance.biz.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import pub.module.finance.api.constants.FcAcLogOpTypeEnum;
import pub.module.finance.api.constants.FcAcLogPayStatusCodeEnum;
import pub.module.finance.api.service.BizFcAccountLogService;
import pub.module.finance.api.service.BizFcAccountService;
import pub.module.finance.api.service.BizPayService;
import pub.module.finance.curd.entity.FcAccount;
import pub.module.finance.curd.entity.FcAccountLog;
import pub.module.finance.curd.service.IFcAccountLogService;
import pub.module.finance.curd.service.IFcAccountService;

import jakarta.annotation.Resource;

@Service
public class BizFcAccountLogServiceImpl implements BizFcAccountLogService {
    @Resource
    IFcAccountLogService fcAccountLogService;
    @Resource
    BizFcAccountService bizFcAccountService;


    @Override
    public boolean getIsPaid(String tradeNo) {
        long countPaid = fcAccountLogService.count(new QueryWrapper<FcAccountLog>().lambda()
                .eq(FcAccountLog::getFcAcLogTradeNo, tradeNo)
                .eq(FcAccountLog::getFcAcLogPayStatusCode, FcAcLogPayStatusCodeEnum.PAID.getCode())
        );
        return countPaid>0;
    }
    @Override
    public FcAccountLog savePaidLog(String tradeNo) {
        FcAccountLog fcAccountLog = fcAccountLogService.getOne(new QueryWrapper<FcAccountLog>().lambda()
                .eq(FcAccountLog::getFcAcLogTradeNo, tradeNo),false);
        fcAccountLog.setFcAcLogPayStatusCode(FcAcLogPayStatusCodeEnum.PAID.getCode());
        fcAccountLogService.updateById(fcAccountLog);
        return fcAccountLog;
    }

    @Override
    public String getFcProductCodeBy(String tradeNo) {
        FcAccountLog fcAccountLog = fcAccountLogService.getOne(new QueryWrapper<FcAccountLog>().lambda()
                .eq(FcAccountLog::getFcAcLogTradeNo, tradeNo),false);
        Assert.notNull(fcAccountLog,"未发起预支付");
        return fcAccountLog.getFcProductCode();
    }

    public void savePayReqLog(BizPayService.PrePayDTO.Req req){
        Assert.notEmpty(req.getTradeNo(), "tradeNo is null");
        Assert.notNull(req.getAmount(), "amount is null");
        Assert.notEmpty(req.getNotifyApi(), "notifyApi is null");
        Assert.notEmpty(req.getFcAcCode(), "fcAcCode is null");
        FcAccount fcAccount = bizFcAccountService.getAccount(req.getFcAcCode());
        Assert.notNull(fcAccount,"账户为空！");
        FcAccountLog fcAccountLog = new FcAccountLog();
        fcAccountLog.setFcAcLogPayStatusCode(FcAcLogPayStatusCodeEnum.NOT_PAY.getCode());
        fcAccountLog.setFcAcSysUserCode(req.getUserCode());
        fcAccountLog.setFcAcLogAmount(req.getAmount());
        fcAccountLog.setFcAcLogNotifyApi(req.getNotifyApi());
        fcAccountLog.setFcAcSysUserRealName(req.getUserRealName());
        fcAccountLog.setFcAcCode(req.getFcAcCode());
        fcAccountLog.setFcAcLogTradeNo(req.getTradeNo());
        fcAccountLog.setFcAcLogOpType(FcAcLogOpTypeEnum.PAY.getCode());
        fcAccountLog.setFcAcLogPeriod(req.getPeriod());
        fcAccountLog.setFcAcLogRemark(req.getRemark());
        fcAccountLog.setFcProductCode(fcAccount.getFcProductCode());
        fcAccountLogService.save(fcAccountLog);
    }


}

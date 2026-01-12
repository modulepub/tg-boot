package pub.module.finance.biz.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.finance.api.constants.FcLoanBillDueStatusCodeEnum;
import pub.module.finance.api.constants.FcLoanBillSettleStatusCodeEnum;
import pub.module.finance.api.service.BizPayNotifyService;
import pub.module.finance.curd.entity.FcAccountLog;
import pub.module.finance.curd.entity.FcLoan;
import pub.module.finance.curd.entity.FcLoanBill;
import pub.module.finance.curd.service.IFcLoanBillService;
import pub.module.finance.curd.service.IFcLoanService;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service("payBill")
public class PayBillServiceImpl implements BizPayNotifyService {

    @Resource
    private IFcLoanBillService fcLoanBillService;
    @Resource
    private IFcLoanService fcLoanService;

    /**
     * 支付成功的回调接口
     *
     * @param fcAccountLog 流水实体
     */
    @Transactional
    @Override
    public void payCallBack(FcAccountLog fcAccountLog) {
        String fcLoanBillCodes = fcAccountLog.getFcAcLogTradeNo();
        String[] billCodeArray = fcLoanBillCodes.split(",");
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (String fcLoanBillCode : billCodeArray) {
            Assert.notEmpty(fcLoanBillCode, "回调处理异常！支付失败！因为该支付的交易号不存在");
            QueryWrapper<FcLoanBill> updateWrapper = new QueryWrapper<>();
            updateWrapper.lambda().eq(FcLoanBill::getFcLoanBillCode, fcLoanBillCode);
            FcLoanBill fcLoanBill = fcLoanBillService.getOne(updateWrapper, false);
            if (FcLoanBillSettleStatusCodeEnum.YES.getCode().equals(fcLoanBill.getFcLoanBillSettleStatusCode())) {
                continue;
            }
            Assert.notNull(fcLoanBill, "回调处理异常！支付失败！因为根据该账单编码查询不到该账单");
            FcLoan fcLoan = fcLoanService.getOne(new QueryWrapper<FcLoan>().lambda().eq(FcLoan::getFcLoanCode, fcLoanBill.getFcLoanCode()), false);
            Assert.notNull(fcLoan, "回调处理异常！支付失败！因为根据该账单编码查询不到该借款");
            fcLoanBill.setFcLoanBillSettleStatusCode(FcLoanBillSettleStatusCodeEnum.YES.getCode());
            fcLoanBill.setFcLoanBillDueStatusCode(FcLoanBillDueStatusCodeEnum.NOT.getCode());
            fcLoanBill.setFcLoanBillSettleAmount(fcLoanBill.getFcLoanBillAmount());
            fcLoanBill.setFcLoanBillRepaymentTime(LocalDateTime.now());
            fcLoanBillService.updateById(fcLoanBill);
            fcLoan.setFcLoanSettleAmount(fcLoan.getFcLoanSettleAmount().add(fcLoanBill.getFcLoanBillAmount()));
            fcLoanService.updateById(fcLoan);
            totalAmount = totalAmount.add(fcLoanBill.getFcLoanBillAmount());
        }
        boolean validateAmountPass = fcAccountLog.getFcAcLogAmount().compareTo(totalAmount) == 0;
        Assert.isTrue(validateAmountPass, "回调处理异常！支付金额与订单金额不匹配！");
    }
}

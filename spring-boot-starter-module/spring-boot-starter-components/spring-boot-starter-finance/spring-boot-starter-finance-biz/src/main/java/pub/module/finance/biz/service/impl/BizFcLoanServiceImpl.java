package pub.module.finance.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pub.module.data.constants.PropertiesConstant;
import pub.module.finance.api.constants.*;
import pub.module.finance.api.event.FcLoanEvent;
import pub.module.finance.api.service.BizFcLoanService;
import pub.module.finance.api.service.BizFcAccountService;
import pub.module.finance.curd.entity.FcAccount;
import pub.module.finance.curd.entity.FcLoan;
import pub.module.finance.curd.entity.FcLoanBill;
import pub.module.finance.curd.entity.FcProduct;
import pub.module.finance.curd.service.IFcAccountService;
import pub.module.finance.curd.service.IFcLoanBillService;
import pub.module.finance.curd.service.IFcLoanService;
import pub.module.finance.curd.service.IFcProductService;
import pub.module.system.api.service.BizSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;


@Slf4j
@Service
@Transactional
public class BizFcLoanServiceImpl implements BizFcLoanService {
    @Resource
    IFcLoanService fcLoanService;
    @Resource
    IFcLoanBillService fcLoanBillService;
    @Resource
    BizFcAccountService bizFcAccountService;
    @Resource
    IFcAccountService fcAccountService;
    @Resource
    BizSysUserService bizSysUserService;
    @Resource
    ApplicationEventPublisher applicationEventPublisher;
    @Resource
    IFcProductService fcProductService;
    @Override
    @Transactional
    public void cashLoanPass(String fcLoanCode) {
        FcLoan fcLoan = fcLoanService.getOne(new QueryWrapper<FcLoan>().lambda().eq(FcLoan::getFcLoanCode, fcLoanCode), false);
        boolean isPass = FcLoanApprovalStatusCodeEnum.PASS.getCode().equals(fcLoan.getFcLoanApprovalStatusCode());
        Assert.isFalse(isPass, "预警：贷款单已审核,发生此异常说明前端没做防抖或者遭遇黑客攻击");
        Assert.notNull(fcLoan, "预警：贷款单不存在,生产阶段发生此异常必然是黑客攻击");
        fcLoan.setFcLoanApprovalStatusCode(FcLoanApprovalStatusCodeEnum.PASS.getCode());
        Assert.notNull(fcLoan.getFcLoanPeriods(), "预警，账单分期不存在，生产阶段发生此异常必然是黑客攻击");
        fcLoan.setFcLoanDisbursementTime(LocalDateTime.now());
        fcLoanService.updateById(fcLoan);
        UserDTO sysUser = bizSysUserService.getUserByUserCode(fcLoan.getUserCode());
        Assert.notNull(sysUser,"严重异常，用户{}不存在！".replace("{}",fcLoan.getUserCode()));
        // 生成账单
        int fcLoanPeriods = fcLoan.getFcLoanPeriods();
        if(fcLoanPeriods==0){
            fcLoanPeriods = 1;
        }
        Assert.notNull( fcLoan.getFcLoanRepayableAmount(),"应还金额为空！");
        BigDecimal periodAmount = fcLoan.getFcLoanRepayableAmount().divide(new BigDecimal(fcLoanPeriods), 2, RoundingMode.HALF_UP);
        BigDecimal aveAmount = fcLoan.getFcLoanApyAmount().divide(new BigDecimal(fcLoanPeriods), 2, RoundingMode.HALF_UP);
        for (int i = 0; i < fcLoanPeriods; i++) {
            FcLoanBill fcLoanBill =  BeanUtil.copyProperties(fcLoan,FcLoanBill.class, PropertiesConstant.BASE_PROPERTIES);
            fcLoanBill.setFcLoanBillDate(LocalDateTime.now().plusMonths(i));
            if(i==0){
                fcLoanBill.setFcLoanBillDueStatusCode(FcLoanBillDueStatusCodeEnum.YES.getCode());//TODO 模拟一条到期的
            }
            if(i==1){
                fcLoanBill.setFcLoanBillDueStatusCode(FcLoanBillDueStatusCodeEnum.YES.getCode());//TODO 模拟一条到期的
                fcLoanBill.setFcLoanBillOverdueStatusCode(FcLoanBillOverdueStatusCodeEnum.YES.getCode());//TODO 模拟一条到期的
            }
            fcLoanBill.setFcLoanBillPrincipalAmount(aveAmount);
            fcLoanBill.setFcLoanCode(fcLoan.getFcLoanCode());
            String realName = sysUser.getUserRealName();
            realName = StrUtil.isNotEmpty(realName)?realName:"用户信息错误";
            fcLoanBill.setFcLoanBillName("{{name}}的第{{num}}分期账单".replace("{{name}}",realName).replace("{{num}}", String.valueOf(i + 1)));
            fcLoanBill.setFcLoanBillInstallmentNum(i + 1);
            fcLoanBill.setUserCode(fcLoan.getUserCode());
            fcLoanBill.setFcProductTypeCode(fcLoan.getFcProductTypeCode());
            //账单日期

            if(i<fcLoanPeriods-1){
                fcLoanBill.setFcLoanBillAmount(periodAmount);
            }else {
                BigDecimal lastBillAmount = fcLoan.getFcLoanRepayableAmount().subtract(periodAmount.multiply(new BigDecimal(i)));
                fcLoanBill.setFcLoanBillAmount(lastBillAmount);
            }
            fcLoanBill.setFcLoanBillInterestRate(fcLoanBill.getFcLoanBillAmount().subtract(aveAmount).divide(fcLoanBill.getFcLoanBillAmount(), 2, RoundingMode.HALF_UP).toString());
            fcLoanBillService.save(fcLoanBill);
        }
        FcAccount fcAccount = bizFcAccountService.getAccount(fcLoan.getUserCode(), fcLoan.getFcProductCode());
        fcAccount.setFcAcPayStatusCode(FcAcPayStatusCodeEnum.NORMAL.getCode());//审核通过后账户较少额度，置为正常
        fcAccountService.updateById(fcAccount);
        //放款
        boolean did = FcLoanReceivedStatusCodeEnum.YES.getCode().equals(fcLoan.getFcLoanReceivedStatusCode());
        Assert.isFalse(did,"预警：重复放款或者前端未做防抖");
        fcLoan.setFcLoanReceivedStatusCode(FcLoanReceivedStatusCodeEnum.YES.getCode());
        fcLoan.setFcLoanDisbursementTime(LocalDateTime.now());
        fcLoan.setFcLoanAmount(fcLoan.getFcLoanApyAmount());
        fcLoanService.updateById(fcLoan);
        fcAccount.setFcAcPayStatusCode(FcAcPayStatusCodeEnum.NORMAL.getCode());//审核通过后账户较少额度，置为正常
        fcAccountService.updateById(fcAccount);
    }

    @Transactional
    @Override
    public void cashLoanReject(String fcLoanCode) {
        FcLoan fcLoan = fcLoanService.getOne(new QueryWrapper<FcLoan>().lambda().eq(FcLoan::getFcLoanCode, fcLoanCode), false);
        Assert.notNull(fcLoan, "预警：贷款单不存在");
        fcLoan.setFcLoanApprovalStatusCode(FcLoanApprovalStatusCodeEnum.REJECT.getCode());
        FcAccount fcAccount = bizFcAccountService.getAccount(fcLoan.getUserCode(), fcLoan.getFcProductCode());
        fcAccount.setFcAcPayStatusCode(FcAcPayStatusCodeEnum.NORMAL.getCode());//审核通过后置为正常
        fcAccountService.updateById(fcAccount);
        fcLoanService.updateById(fcLoan);
    }





    @Transactional
    @Override
    public void apply(FcLoanDTO fcLoanDTO) {
        UserDTO sysUser = bizSysUserService.getUserByUserCode(fcLoanDTO.getUserCode());
        Assert.notNull(sysUser,"严重异常，未接收到用户信息！userCode="+fcLoanDTO.getUserCode());
        FcLoan fcLoan = BeanUtil.copyProperties(fcLoanDTO, FcLoan.class);
        fcLoan.setUserCode(sysUser.getUserCode());
        FcProduct fcProduct = fcProductService.getOne(new QueryWrapper<FcProduct>().lambda().eq(FcProduct::getFcProductCode, fcLoan.getFcProductCode()), false);
        Assert.notNull(fcProduct, "系统告警：产品不存在");
        this.setFcLoan(sysUser, fcProduct, fcLoan, FcLoanTypeCodeEnum.US_CREDIT.getCode());
        //应还金额
        BigDecimal borrowYears = new BigDecimal(fcLoan.getFcLoanPeriods()).divide(new BigDecimal("12"),2,RoundingMode.HALF_UP);
        BigDecimal yearInterestRate = fcLoan.getFcLoanYearInterestRate();
        BigDecimal borrowAmount = fcLoan.getFcLoanApyAmount();
        Assert.isFalse(BigDecimal.ZERO.compareTo(borrowAmount)>=0,"借款金额不能为0");
        BigDecimal interestAmount = borrowAmount.multiply(yearInterestRate).multiply(borrowYears);
        fcLoan.setFcLoanRepayableAmount(borrowAmount.add(interestAmount));
        fcLoan.setFcLoanApprovalStatusCode(FcLoanApprovalStatusCodeEnum.ING.getCode());
        fcLoanService.save(fcLoan);
        //获取信用账户扣额度
        FcAccount fcAccount = bizFcAccountService.getAccount(
                fcLoan.getUserCode(),
                fcLoan.getFcProductCode()
        );
        boolean isNormal = FcAcPayStatusCodeEnum.NORMAL.getCode().equals(fcAccount.getFcAcPayStatusCode());
        Assert.isTrue(isNormal, "借/还贷流程中,不允许再次借款");//TODO 方便调试，暂停校验
        fcAccount.setFcAcPayStatusCode(FcAcPayStatusCodeEnum.ING.getCode());
        BigDecimal fcAcBalance = fcAccount.getFcAcBalance().subtract(fcLoan.getFcLoanApyAmount());
        String msg =   "额度不足，可用额度为{{fcAcBalance}},使用额度{{fcLoanApyAmount}}"
                .replace("{{fcAcBalance}}", fcAccount.getFcAcBalance().toString())
                .replace("{{fcLoanApyAmount}}", fcLoan.getFcLoanApyAmount().toString());
        Assert.isFalse(BigDecimal.ZERO.compareTo(fcAcBalance)>0, msg);
        fcAccount.setFcAcBalance(fcAcBalance);//额度减少
        fcAccountService.updateById(fcAccount);
        applicationEventPublisher.publishEvent(new FcLoanEvent(fcLoan));
    }



    private void setFcLoan(UserDTO sysUser, FcProduct fcProduct, FcLoan fcLoan, String fcLoanTypeCode){
        fcLoan.setUserCode(sysUser.getUserCode());
        String realName = sysUser.getUserRealName();
        realName = StrUtil.isNotEmpty(realName)?realName:"用户信息错误";
        String fcLoanName = "{{realName}}的{{fcLoanTypeCode}}申请";
        fcLoanName = fcLoanName.replace("{{realName}}",realName);
        fcLoanName = fcLoanName.replace("{{fcLoanTypeCode}}", FcLoanTypeCodeEnum.getTextByCode(fcLoanTypeCode));
        fcLoan.setFcLoanName(fcLoanName);
        fcLoan.setFcProductTypeCode(fcProduct.getFcProductTypeCode());
        fcLoan.setFcLoanTypeCode(fcLoanTypeCode);
        fcLoan.setFcProductCode(fcProduct.getFcProductCode());
        fcLoan.setFcProductTypeCode(fcProduct.getFcProductTypeCode());
        fcLoan.setFcLoanYearInterestRate(fcProduct.getFcProductYearInterestRate());
        fcLoan.setFcLoanApplyTime(LocalDateTime.now());
    }


}

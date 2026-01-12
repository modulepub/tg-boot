package pub.module.finance.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pub.module.finance.api.constants.*;
import pub.module.finance.api.event.FcCreditExtensionEvent;
import pub.module.finance.api.service.BizFcAccountService;
import pub.module.finance.api.service.BizFcCreditExtensionService;
import pub.module.finance.curd.entity.FcAccount;
import pub.module.finance.curd.entity.FcCreditExtension;
import pub.module.finance.curd.entity.FcProduct;
import pub.module.finance.curd.service.IFcAccountService;
import pub.module.finance.curd.service.IFcCreditExtensionService;
import pub.module.finance.curd.service.IFcProductService;
import pub.module.system.api.service.BizSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class BizFcCreditExtensionServiceImpl implements BizFcCreditExtensionService {
    @Resource
    private IFcCreditExtensionService creditExtensionService;
    @Resource
    BizFcAccountService bizFcAccountService;
    @Resource
    IFcAccountService fcAccountService;
    @Resource
    IFcProductService fcProductService;
    @Resource
    BizSysUserService bizSysUserService;
    @Resource
    ApplicationEventPublisher applicationEventPublisher;
    @Override
    public void creditLoanReject(String fcCdExCode) {
        FcCreditExtension fcCreditExtension = creditExtensionService.getOne(new QueryWrapper<FcCreditExtension>().lambda().eq(FcCreditExtension::getFcCdExCode, fcCdExCode), false);
        fcCreditExtension.setFcCdExApprovalStatusCode(FcLoanApprovalStatusCodeEnum.REJECT.getCode());
        FcAccount fcAccount = bizFcAccountService.getAccount(
                fcCreditExtension.getUserCode(),
                fcCreditExtension.getFcProductCode()
        );
        fcAccount.setFcAcCreditStatusCode(FcAcCreditStatusCodeEnum.REJECT.getCode());
        fcAccountService.updateById(fcAccount);
        creditExtensionService.updateById(fcCreditExtension);
    }
    @Transactional
    @Override
    public void creditLoanPass(String fcCdExCode) {
        FcCreditExtension fcCreditExtension = creditExtensionService.getOne(new QueryWrapper<FcCreditExtension>().lambda().eq(FcCreditExtension::getFcCdExCode, fcCdExCode), false);
        fcCreditExtension.setFcCdExApprovalStatusCode(FcCdExApprovalStatusCodeEnum.PASS.getCode());
        // 随机给假额度
        fcCreditExtension.setFcCdExAmount(getRandomAmount());
        Assert.isTrue(BigDecimal.ZERO.compareTo(fcCreditExtension.getFcCdExAmount()) <= 0,"授信额度不能为0");
        fcCreditExtension.setFcCdExApprovalTime(new Date());
        creditExtensionService.updateById(fcCreditExtension);
        FcAccount fcAccount = bizFcAccountService.getAccount(
                fcCreditExtension.getUserCode(),
                fcCreditExtension.getFcProductCode()
        );
        fcAccount.setFcAcPayStatusCode(FcAcPayStatusCodeEnum.NORMAL.getCode());
        fcAccount.setFcAcCreditStatusCode(FcAcCreditStatusCodeEnum.PASSED.getCode());
        fcAccount.setFcAcBalance(fcCreditExtension.getFcCdExAmount());
        fcAccountService.updateById(fcAccount);
    }
    private static BigDecimal getRandomAmount(){
        int r = RandomUtil.randomInt(8000,50000);
        r = r/1000;
        r = r*1000;
        return new BigDecimal(r);
    }

    public void credit(CreditApplyDTO creditApplyDTO) {
        UserDTO sysUser = bizSysUserService.getUserByUserCode(creditApplyDTO.getUserCode());
        Assert.notNull(sysUser, "系统预警：用户不存在！生产阶段为系统入侵！userCode=" + creditApplyDTO.getUserCode());
        String[] fcProductCodeList = creditApplyDTO.getFcProductCode().split(",");
        for (String fcProductCode : fcProductCodeList) {
            Assert.notEmpty(fcProductCode, "系统预警：产品编码不能为空！生产阶段为系统入侵！");
            FcCreditExtension fcCreditExtension = new FcCreditExtension();
            fcCreditExtension.setFcProductCode(fcProductCode);
            FcProduct fcProduct = fcProductService.getOne(new QueryWrapper<FcProduct>().lambda().eq(FcProduct::getFcProductCode, fcCreditExtension.getFcProductCode()), false);
            this.setFcCreditExtension(sysUser, fcProduct, fcCreditExtension, FcLoanTypeCodeEnum.CREDIT.getCode());
            fcCreditExtension.setFcCdExApprovalStatusCode(FcLoanApprovalStatusCodeEnum.ING.getCode());
            //TODO 给假额度 BEGIN
            fcCreditExtension.setFcCdExAmount(BigDecimal.ZERO);
            //TODO 给假额度 END
            creditExtensionService.save(fcCreditExtension);
            //获取信用账户状态为审核中
            FcAccount fcAccount = bizFcAccountService.getAccount(
                    fcCreditExtension.getUserCode(),
                    fcCreditExtension.getFcProductCode()
            );
            fcAccount.setFcAcPayStatusCode(FcAcPayStatusCodeEnum.NORMAL.getCode());
            fcAccount.setFcAcCreditStatusCode(FcAcCreditStatusCodeEnum.ING.getCode());
            fcAccount.setFcAcBalance(fcCreditExtension.getFcCdExAmount());
            fcAccountService.updateById(fcAccount);
            applicationEventPublisher.publishEvent(new FcCreditExtensionEvent(fcCreditExtension));
        }
    }

    private void setFcCreditExtension(UserDTO sysUser, FcProduct fcProduct, FcCreditExtension fcCreditExtension, String fcLoanTypeCode){
        String realName = sysUser.getUserRealName();
        fcCreditExtension.setUserCode(sysUser.getUserCode());
        realName = StrUtil.isNotEmpty(realName)?realName:"用户信息错误";
        String fcCreditExtensionName = "{{realName}}的{{fcLoanTypeCode}}申请";
        fcCreditExtensionName = fcCreditExtensionName.replace("{{realName}}",realName);
        fcCreditExtensionName = fcCreditExtensionName.replace("{{fcLoanTypeCode}}", FcLoanTypeCodeEnum.getTextByCode(fcLoanTypeCode));
        fcCreditExtension.setFcCdExName(fcCreditExtensionName);
        fcCreditExtension.setFcProductTypeCode(fcProduct.getFcProductTypeCode());
        fcCreditExtension.setFcProductCode(fcProduct.getFcProductCode());
        fcCreditExtension.setFcProductTypeCode(fcProduct.getFcProductTypeCode());
        fcCreditExtension.setFcCdExYearInterestRate(fcProduct.getFcProductYearInterestRate());
        fcCreditExtension.setFcCdExApplyTime(new Date());
    }
}

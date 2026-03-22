package pub.module.finance.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.finance.api.constants.*;
import pub.module.finance.api.dto.FcAccountDTO;
import pub.module.finance.api.service.BizBankAccountService;
import pub.module.finance.curd.entity.FcAccount;
import pub.module.finance.curd.service.IFcAccountService;

import jakarta.annotation.Resource;
import java.math.BigDecimal;


/**
 * 金融账户管理
 *
 * @author tg
 * @version V1.0
 * @since 2025-09-30
 */
@Slf4j
@Service
public class BizKqBankAccountServiceImpl implements BizBankAccountService {

    @Resource
    IFcAccountService fcAccountService;
    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    @Override
    public FcAccountDTO bindBankCardSms(BindCardSmsDTO bindCardSmsDTO) {
        String userCode = bindCardSmsDTO.getFcAcSysUserCode();
        Assert.notEmpty("userCode is not null", userCode);
        FcAccount fcAccount = fcAccountService.getOne(new QueryWrapper<FcAccount>().lambda()
                .eq(FcAccount::getFcBankCardNo, bindCardSmsDTO.getFcBankCardNo())
                .eq(FcAccount::getFcProductTypeCode, FcProductTypeCodeEnum.BANK.getCode())
                .eq(FcAccount::getFcAcBindCardStatusCode, FcAcBindCardStatusCodeEnum.YES.getCode())
        );
        if (fcAccount == null) {
            fcAccount = BeanUtil.copyProperties(bindCardSmsDTO, FcAccount.class);
            Assert.notEmpty(bindCardSmsDTO.getFcProductCode(), "预警：支付产品参数不能为空！");
            fcAccount.setFcProductTypeCode(FcProductTypeCodeEnum.BANK.getCode());
            Assert.notEmpty(fcAccount.getFcBankCardNo(), "fcBankCardNo is not null");
            Assert.notEmpty(fcAccount.getFcBankCardPhone(), "fcBankCardPhone is not null");
            fcAccount.setFcAcBalance(BigDecimal.ZERO);
            fcAccount.setFcAcSysUserCode(userCode);
            fcAccount.setFcBankName(bindCardSmsDTO.getFcBankName());
            String params = "";
            fcAccount.setFcAcPayParam(params);
            fcAccountService.save(fcAccount);
            fcAccount.setFcAcBindCardStatusCode(FcAcBindCardStatusCodeEnum.NOT.getCode());
        } else {
            throw new RuntimeException("该银行卡已绑定！");
        }
        return BeanUtil.copyProperties(fcAccount, FcAccountDTO.class);
    }


    @Override
    public FcAccountDTO bindBankCardSure(BindBankCardSureDTO bindBankCardSureDTO) {
        Assert.notEmpty(bindBankCardSureDTO.getFcAcSysUserCode(), "userCode is null！");
        Assert.notEmpty(bindBankCardSureDTO.getFcBankCardAuthCode1(), "验证码为空!");
        QueryWrapper<FcAccount> fcAccountQueryWrapper = new QueryWrapper<>();
        fcAccountQueryWrapper.lambda().eq(FcAccount::getFcAcCode, bindBankCardSureDTO.getFcAcCode());
        FcAccount fcAccount = fcAccountService.getOne(fcAccountQueryWrapper, false);
        Assert.notNull(fcAccount, "请先发送绑卡短信！");
        try {
            JSONObject params = JSONUtil.parseObj(fcAccount.getFcAcPayParam());
//            kqPayUtilR.bindCard(
//                    fcAccount.getFcAcSysUserCode(),
//                    fcAccount.getFcBankCardNo(),
//                    fcAccount.getFcBankCardPhone(),
//                    bindBankCardSureDTO.getFcBankCardAuthCode1(),
//                    params.getStr("token"),
//                    fcAccount.getId()
//            );

        } catch (Exception e) {
            log.error("绑卡失败，验证码校验失败！", e);
            //TODO 临时调试，后续要抛出的！
        }
        fcAccount.setFcAcBindCardStatusCode(FcAcBindCardStatusCodeEnum.YES.getCode());
        fcAccountService.updateById(fcAccount);
        return BeanUtil.copyProperties(fcAccount, FcAccountDTO.class);
    }


}

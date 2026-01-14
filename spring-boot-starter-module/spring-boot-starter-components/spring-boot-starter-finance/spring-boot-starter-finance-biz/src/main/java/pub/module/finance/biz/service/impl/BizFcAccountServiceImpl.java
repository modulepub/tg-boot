package pub.module.finance.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.finance.api.dto.FcAccountDTO;
import pub.module.finance.api.dto.FcAccountLogDTO;
import pub.module.finance.api.service.BizFcAccountService;
import pub.module.finance.curd.entity.FcAccount;
import pub.module.finance.curd.entity.FcAccountLog;
import pub.module.finance.curd.entity.FcProduct;
import pub.module.finance.curd.service.IFcAccountService;
import pub.module.finance.curd.service.IFcProductService;
import pub.module.system.api.service.BizSysUserService;
import pub.module.system.api.service.dto.UserDTO;

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
public class BizFcAccountServiceImpl implements BizFcAccountService {

    @Resource
    IFcAccountService fcAccountService;
    @Resource
    IFcProductService fcProductService;
    @Resource
    BizSysUserService bizSysUserService;




    @Transactional
    @Override
    public FcAccountDTO getAccount(String userCode, String fcProductCode) {
        QueryWrapper<FcAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FcAccount::getFcAcSysUserCode, userCode);
        queryWrapper.lambda().eq(FcAccount::getFcProductCode, fcProductCode);
        FcAccount fcAccount = fcAccountService.getOne(queryWrapper, false);
        if (fcAccount == null) {
            fcAccount = new FcAccount();
            fcAccount.setFcAcSysUserCode(userCode);
            fcAccount.setFcAcBalance(BigDecimal.ZERO);
            fcAccount.setFcAcBalance(BigDecimal.ZERO);
            if (StrUtil.isNotEmpty(fcProductCode)) {
                FcProduct fcProduct = fcProductService.getOne(new QueryWrapper<FcProduct>().lambda().eq(FcProduct::getFcProductCode, fcProductCode), false);
                Assert.notNull(fcProduct, "入侵预警：产品不存在，非法的产品编码,初始化金融账户时候");
                fcAccount.setFcProductCode(fcProductCode);
                fcAccount.setFcProductTypeCode(fcProduct.getFcProductTypeCode());
            }
            fcAccountService.save(fcAccount);
        }
        if(StrUtil.isEmpty(fcAccount.getFcAcSysUserRealName())){
            UserDTO sysUser = bizSysUserService.getUserByUserCode(fcAccount.getFcAcSysUserCode());
            Assert.notNull(sysUser,"严重异常：主动补偿用户信息发生错误，未查询到该用户信息！");
            fcAccount.setFcAcSysUserRealName(sysUser.getUserRealName());
            fcAccount.setFcAcIdCardNo(sysUser.getUserIdCardNum());
            fcAccountService.updateById(fcAccount);
        }
        return BeanUtil.copyProperties(fcAccount, FcAccountDTO.class);
    }

    @Override
    public FcAccountDTO getAccount(String fcAcCode) {
        QueryWrapper<FcAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FcAccount::getFcAcCode, fcAcCode);
        FcAccount result = fcAccountService.getOne(queryWrapper, false);
        Assert.notNull(result, "入侵预警：账户不存在，非法的账户编码");
        log.info("获取到账户：{}", result);
        return BeanUtil.copyProperties(result, FcAccountDTO.class);
    }

    @Override
    public FcAccountDTO bindBankCardSms(BindCardSmsDTO bindCardSmsDTO) {
        return null;
    }

    @Override
    public FcAccountDTO bindBankCardSure(BindBankCardSureDTO bindBankCardSureDTO) {
        return null;
    }

    @Override
    public void bankPay(FcAccountLogDTO fcAccountLog) {

    }


}

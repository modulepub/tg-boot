package pub.module.system.biz.service.impl;

import jakarta.annotation.Resource;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import pub.module.system.api.service.*;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pub.module.system.api.vo.SysVerificationDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Api 验证码 Service
 *
 * @author tg
 * 2026-04-20 14:14:27
 */
@Service
@Validated
@CacheConfig(cacheNames = "sysVerification")
public class ApiSysVerificationServiceImpl implements ApiSysVerificationService {

    //    @Resource
//    SysVerificationService sysVerificationService;
    @Resource
    private CacheManager cacheManager;

    @Override
    @CachePut(key = "#verificationTypeCode + ':' + #verificationKey")
    public SysVerificationDTO set(String verificationTypeCode, String verificationKey, String verificationValue, LocalDateTime verificationExpireTime) {
//        Assert.notEmpty(verificationTypeCode,"verificationTypeCode not null");
//        Assert.notEmpty(verificationKey,"verificationKey not null");
//        Assert.notEmpty(verificationValue,"verificationValue not null");
//        Assert.notNull(verificationExpireTime,"verificationExpireTime not null");
//        SysVerification sysVerification = new SysVerification();
//        sysVerification.setVerificationExpireTime(verificationExpireTime);
//        sysVerification.setVerificationTypeCode(verificationTypeCode);
//        sysVerification.setVerificationValue(verificationValue);
//        sysVerification.setVerificationKey(verificationKey);
//        sysVerificationService.save(sysVerification);
//        BeanUtils.copyProperties(sysVerification, result);
        SysVerificationDTO sysVerificationDTO = new SysVerificationDTO();
        sysVerificationDTO.setVerificationTypeCode(verificationTypeCode);
        sysVerificationDTO.setVerificationKey(verificationKey);
        sysVerificationDTO.setVerificationValue(verificationValue);
        sysVerificationDTO.setVerificationExpireTime(verificationExpireTime);
        return sysVerificationDTO;
    }

    @Override
    @Cacheable(key = "#verificationTypeCode + ':' + #verificationKey", unless = "#result == null")
    public SysVerificationDTO getByKey(String verificationTypeCode, String verificationKey) {
//        QueryWrapper<SysVerification> sysVerificationQueryWrapper = new QueryWrapper<>();
//        sysVerificationQueryWrapper.lambda().eq(SysVerification::getVerificationTypeCode,verificationTypeCode);
//        sysVerificationQueryWrapper.lambda().eq(SysVerification::getVerificationKey,verificationKey);
//        SysVerification sysVerification = sysVerificationService.getOne(sysVerificationQueryWrapper,false);
//        Assert.notNull(sysVerification,"请生成验证码"+verificationTypeCode+verificationKey);
//        return BeanUtil.copyProperties(sysVerification,SysVerificationDTO.class);
        return null;
    }

    @Override
    @CacheEvict(key = "#verificationTypeCode + ':' + #verificationKey")
    public void delByKey(String verificationTypeCode, String verificationKey) {
//        sysVerificationService.remove(new QueryWrapper<SysVerification>().lambda().eq(SysVerification::getVerificationTypeCode,verificationTypeCode).eq(SysVerification::getVerificationKey,verificationKey));
    }
}

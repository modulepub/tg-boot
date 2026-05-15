package pub.module.system.biz.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.lang.Assert;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pub.module.system.api.constants.VerificationTypeCodeEnum;
import pub.module.system.api.service.ApiCaptchaService;
import pub.module.system.api.service.ApiSysVerificationService;
import pub.module.system.api.vo.SysCaptchaVO;
import pub.module.system.api.vo.SysVerificationDTO;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 验证码 Service 实现
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Service
@AllArgsConstructor
public class ApiCaptchaServiceImpl implements ApiCaptchaService {

    @Resource
    ApiSysVerificationService apiSysVerificationService;
    @Override
    public SysCaptchaVO generate() {
        // 生成验证码 key
        String key = UUID.randomUUID().toString();
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(130, 48, 4, 2);
        String code = captcha.getCode();
        String image = captcha.getImageBase64Data();
        // 保存到缓存
        apiSysVerificationService.set(VerificationTypeCodeEnum.CAPTCHA.getCode(),key, code,LocalDateTime.now().plusMinutes(10));
        // 封装返回数据
        SysCaptchaVO captchaVO = new SysCaptchaVO();
        captchaVO.setKey(key);
        captchaVO.setImage(image);

        return captchaVO;
    }

    @Override
    public void validate(String key, String code) {
        // 如果关闭了验证码，则直接效验通过
        if (!isCaptchaEnabled()) {
            return;
        }
        Assert.notEmpty(key, "请输入 key");
        Assert.notEmpty(code, "请输入 验证码");
        // 获取验证码
        SysVerificationDTO sysVerificationDTO =apiSysVerificationService.getByKey(VerificationTypeCodeEnum.CAPTCHA.getCode(),key);
        Assert.notNull(sysVerificationDTO,"还未生成验证码");
        Assert.isTrue(LocalDateTime.now().isBefore(sysVerificationDTO.getVerificationExpireTime()),"验证码已过期");
        Assert.isTrue(code.equalsIgnoreCase(sysVerificationDTO.getVerificationValue()), "验证码错误");
        apiSysVerificationService.delByKey(VerificationTypeCodeEnum.CAPTCHA.getCode(), key);
    }

    @Override
    public boolean isCaptchaEnabled() {
        return true;
    }

}

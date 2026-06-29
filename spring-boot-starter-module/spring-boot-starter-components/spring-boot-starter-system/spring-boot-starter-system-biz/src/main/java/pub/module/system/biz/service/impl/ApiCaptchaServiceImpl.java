package pub.module.system.biz.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.lang.Assert;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.common.cache.TgEphemeralCache;
import pub.module.system.api.service.ApiCaptchaService;
import pub.module.system.api.vo.SysCaptchaVO;
import pub.module.system.biz.cache.SystemCacheNamespaces;

import java.time.Duration;
import java.util.UUID;

@Service
public class ApiCaptchaServiceImpl implements ApiCaptchaService {

    @Resource
    private TgEphemeralCache tgEphemeralCache;

    @Override
    public SysCaptchaVO generate() {
        String key = UUID.randomUUID().toString();
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(130, 48, 4, 2);
        String code = captcha.getCode();
        String image = captcha.getImageBase64Data();
        tgEphemeralCache.put(SystemCacheNamespaces.CAPTCHA, key, code, Duration.ofMinutes(10));
        SysCaptchaVO captchaVO = new SysCaptchaVO();
        captchaVO.setKey(key);
        captchaVO.setImage(image);
        return captchaVO;
    }

    @Override
    public void validate(String key, String code) {
        if (!isCaptchaEnabled()) {
            return;
        }
        Assert.notEmpty(key, "请输入 key");
        Assert.notEmpty(code, "请输入 验证码");
        String cached = tgEphemeralCache.get(SystemCacheNamespaces.CAPTCHA, key, String.class);
        Assert.notNull(cached, "还未生成验证码");
        Assert.isTrue(code.equalsIgnoreCase(cached), "验证码错误");
        tgEphemeralCache.evict(SystemCacheNamespaces.CAPTCHA, key);
    }

    @Override
    public boolean isCaptchaEnabled() {
        return true;
    }
}

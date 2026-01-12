package pub.module.system.biz.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pub.module.cache.api.service.BizCacheService;
import pub.module.system.api.service.BizCaptchaService;
import pub.module.system.api.vo.SysCaptchaVO;

import java.util.concurrent.TimeUnit;

/**
 * 验证码 Service 实现
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Service
@AllArgsConstructor
public class BizCaptchaServiceImpl implements BizCaptchaService {

    @Override
    public SysCaptchaVO generate() {
        // 生成验证码 key
        String key = UUID.randomUUID().toString();

        // 生成验证码
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(130, 48, 4, 2);
        String code = captcha.getCode();
        String image = captcha.getImageBase64Data();

        // 保存到缓存
        SpringUtil.getBean(BizCacheService.class).set(key, code, 300, TimeUnit.SECONDS);

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
        String captcha = SpringUtil.getBean(BizCacheService.class).get(key);
        // 效验成功
        Assert.isTrue(code.equalsIgnoreCase(captcha), "验证码错误");
        SpringUtil.getBean(BizCacheService.class).delete(key);
    }

    @Override
    public boolean isCaptchaEnabled() {
        return true;
    }

}

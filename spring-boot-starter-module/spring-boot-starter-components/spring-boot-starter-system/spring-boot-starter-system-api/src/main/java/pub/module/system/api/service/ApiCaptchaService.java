package pub.module.system.api.service;


import pub.module.system.api.vo.SysCaptchaVO;

/**
 * 验证码
 *
 * @author 阿沐 babamu@126.com
 * <a href="https://maku.net">MAKU</a>
 */
public interface ApiCaptchaService {
    /**
     * 生成验证码
     */
    SysCaptchaVO generate();

    /**
     * 验证码效验
     *
     * @param key  key
     * @param code 验证码
     */
    void validate(String key, String code);

    /**
     * 是否开启登录验证码
     *
     * @return true：开启  false：关闭
     */
    boolean isCaptchaEnabled();
}

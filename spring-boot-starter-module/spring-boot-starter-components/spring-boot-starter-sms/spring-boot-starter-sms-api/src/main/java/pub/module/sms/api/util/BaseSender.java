package pub.module.sms.api.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import pub.module.sms.api.config.SmsProperties;
import pub.module.sms.api.constants.MarketingType;
import pub.module.sms.api.constants.NoticeType;
import pub.module.sms.api.constants.SmsType;

/**
 * 短信发送器抽象基类
 * 优化说明：
 * 1. 移除硬编码的模板常量，改用配置文件管理
 * 2. 移除 SmsAccountConfig，改用 SmsProperties 统一配置
 * 3. 简化代码结构，提高可维护性
 */
@Slf4j
public abstract class BaseSender {

    @Autowired
    protected SmsProperties smsProperties;

    /**
     * 发送登录/注册/支付验证码
     * @param phone 手机号
     * @param code  验证码
     * @param smsType  0-登录 1-注册 3-支付
     * @return 接口响应
     */
    public JSONObject sendSms(String phone, String code, SmsType smsType) {
        if (smsType == null) {
            log.warn("未知的验证码类型：{}", smsType);
            return null;
        }

        String content = getVerificationTemplate(smsType).replace("{code}", code);
        log.info("用户{}发送{}：{}", phone, smsType.getDesc(), code);

        return sendPost(phone, content,
                       getCaptchaUsername(),
                       getCaptchaPassword());
    }

    /**
     * 发送通知短信（带参数）
     * @param phone  手机号
     * @param noticeType   通知类型（1-4）
     * @param params 模板参数
     */
    public void sendNoticeSms(String phone, NoticeType noticeType, JSONObject params) {
        if (noticeType == null) {
            log.warn("未知的通知类型：{}", noticeType);
            return;
        }

        String content = getNoticeTemplate(noticeType);
        content = replaceTemplateParams(content, params);

        log.info("向用户{}发送通知短信（{}）：{}", phone, noticeType.getDesc(), content);
        sendPost(phone, content,
                getNoticeUsername(),
                getNoticePassword());
    }

    /**
     * 发送营销短信
     * @param phone 手机号
     * @param marketingType  营销类型（1-2）
     */
    public void sendMarketingSms(String phone, MarketingType marketingType) {
        if (marketingType == null) {
            log.warn("未知的营销类型：{}", marketingType);
            return;
        }

        String content = getMarketingTemplate(marketingType);
        log.info("向用户{}发送营销短信（{}）：{}", phone, marketingType.getDesc(), content);

        sendPost(phone, content,
                getMarketingUsername(),
                getMarketingPassword());
    }

    /**
     * 发送POST请求的具体实现（子类需实现各自平台的发送逻辑）
     * @param phone    手机号
     * @param content  短信内容
     * @param username 账号
     * @param password 密码
     * @return 接口响应
     */
    protected abstract JSONObject sendPost(String phone, String content, String username, String password);

    /**
     * 获取验证码账号（子类实现）
     */
    protected abstract String getCaptchaUsername();

    /**
     * 获取验证码密码（子类实现）
     */
    protected abstract String getCaptchaPassword();

    /**
     * 获取通知账号（子类实现）
     */
    protected abstract String getNoticeUsername();

    /**
     * 获取通知密码（子类实现）
     */
    protected abstract String getNoticePassword();

    /**
     * 获取营销账号（子类实现）
     */
    protected abstract String getMarketingUsername();

    /**
     * 获取营销密码（子类实现）
     */
    protected abstract String getMarketingPassword();

    /**
     * 获取验证码模板
     */
    protected String getVerificationTemplate(SmsType type) {
        String template = smsProperties.getTemplate().getCaptcha().get(type.getTemplateKey());
        if (template == null) {
            log.warn("未找到验证码模板：{}", type.getTemplateKey());
            return "您的验证码为{code}，请在有效时间内提交验证码完成验证。";
        }
        return template;
    }

    /**
     * 获取通知模板
     */
    protected String getNoticeTemplate(NoticeType type) {
        String template = smsProperties.getTemplate().getNotice().get(type.getTemplateKey());
        if (template == null) {
            log.warn("未找到通知模板：{}", type.getTemplateKey());
            throw new IllegalArgumentException("不支持的通知类型: " + type);
        }
        return template;
    }

    /**
     * 获取营销模板
     */
    protected String getMarketingTemplate(MarketingType type) {
        String template = smsProperties.getTemplate().getMarketing().get(type.getTemplateKey());
        if (template == null) {
            log.warn("未找到营销模板：{}", type.getTemplateKey());
            throw new IllegalArgumentException("不支持的营销类型: " + type);
        }
        return template;
    }

    /**
     * 替换模板中的变量
     * @param content 模板内容
     * @param params 变量参数
     * @return 替换后的内容
     */
    protected String replaceTemplateParams(String content, JSONObject params) {
        if (params != null) {
            for (String key : params.keySet()) {
                content = content.replace("{" + key + "}", params.getString(key));
            }
        }
        return content;
    }
}

package pub.module.sms.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 短信配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "hhshop.sms")
public class SmsProperties {

    /** 是否开启短信功能 */
    private Boolean open = false;

    /** 验证码配置 */
    private CaptchaConfig captcha = new CaptchaConfig();

    /** 短信渠道配置 */
    private ChannelConfig channel = new ChannelConfig();

    /** 短信模板配置 */
    private TemplateConfig template = new TemplateConfig();

    /** Redis键前缀配置 */
    private RedisKeyConfig redisKey = new RedisKeyConfig();

    /**
     * 验证码配置
     */
    @Data
    public static class CaptchaConfig {
        /** 验证码有效期（秒） */
        private Long expireSeconds = 300L;
        /** 发送间隔（秒） */
        private Long sendIntervalSeconds = 60L;
        /** 最大重试次数 */
        private Integer maxRetryCount = 3;
        /** 验证码长度 */
        private Integer codeLength = 6;
    }

    /**
     * 短信渠道配置
     */
    @Data
    public static class ChannelConfig {
        /** 创蓝短信配置 */
        private ChuangLanConfig chuanglan = new ChuangLanConfig();
        /** 玄武短信配置 */
        private XuanWuConfig xuanwu = new XuanWuConfig();

        @Data
        public static class ChuangLanConfig {
            /** API地址 */
            private String apiUrl = "https://smssh1.253.com/msg/v1/send/json";
            /** 验证码账号 */
            private String captchaUsername;
            /** 验证码密码 */
            private String captchaPassword;
            /** 通知账号 */
            private String noticeUsername;
            /** 通知密码 */
            private String noticePassword;
            /** 营销账号 */
            private String marketingUsername;
            /** 营销密码 */
            private String marketingPassword;
            /** 连接超时（毫秒） */
            private Integer connectTimeout = 10000;
            /** 读取超时（毫秒） */
            private Integer readTimeout = 30000;
        }

        @Data
        public static class XuanWuConfig {
            /** API地址 */
            private String apiUrl = "https://sh.mosapi.cn:9000/api/v1.0.0/message/group/send";
            /** 验证码账号 */
            private String captchaUsername;
            /** 验证码密码 */
            private String captchaPassword;
            /** 通知账号 */
            private String noticeUsername;
            /** 通知密码 */
            private String noticePassword;
            /** 营销账号 */
            private String marketingUsername;
            /** 营销密码 */
            private String marketingPassword;
            /** 连接超时（毫秒） */
            private Integer connectTimeout = 10000;
            /** 读取超时（毫秒） */
            private Integer readTimeout = 30000;
        }
    }

    /**
     * 短信模板配置
     */
    @Data
    public static class TemplateConfig {
        /** 验证码模板 */
        private Map<String, String> captcha;
        /** 通知模板 */
        private Map<String, String> notice;
        /** 营销模板 */
        private Map<String, String> marketing;
    }

    /**
     * Redis键前缀配置
     */
    @Data
    public static class RedisKeyConfig {
        /** 通用验证码前缀 */
        private String commonCode = "sys:sms:common:code:";
        /** 登录验证码前缀 */
        private String loginCode = "sys:sms:login:code:";
        /** 登录发送间隔前缀 */
        private String loginInterval = "sys:sms:login:interval:";
        /** 登录重试次数前缀 */
        private String loginRetry = "sys:sms:login:retry:";
        /** 注册验证码前缀 */
        private String registerCode = "sys:sms:register:code:";
        /** 注册发送间隔前缀 */
        private String registerInterval = "sys:sms:register:interval:";
        /** 注册重试次数前缀 */
        private String registerRetry = "sys:sms:register:retry:";
        /** 支付验证码前缀 */
        private String payCode = "sys:sms:pay:code:";
        /** 支付发送间隔前缀 */
        private String payInterval = "sys:sms:pay:interval:";
        /** 支付重试次数前缀 */
        private String payRetry = "sys:sms:pay:retry:";
    }
}


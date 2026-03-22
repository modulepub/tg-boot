package pub.module.wx.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信配置属性类
 * 从配置文件中读取微信相关配置信息
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.mini")
public class WeChatProperties {

    /**
     * 微信小程序appid
     */
    private String appId;
    /**
     * 微信小程序appSecret
     */
    private String appSecret;

    private String  msgDataFormat;

    /**
     * 微信支付商户号
     */
    private String mchId;

    /**
     * 微信支付商户密钥
     */
    private String mchKey;

    private String privateKeyPath;

    private String privateCertPath;

    private String notifyUrl;
    private String subAppId;
    private String subMchId;

}

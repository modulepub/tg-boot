package pub.module.wx.biz.config;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信公众号 WxMpService Bean（独立配置，避免与消息 Handler / RuntimeRefresher 循环依赖）。
 */
@Configuration
public class WxMpServiceConfiguration {

    @Bean
    public WxMpService wxMpService() {
        return new WxMpServiceImpl();
    }
}

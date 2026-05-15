package pub.module.trade.biz.config;

import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置
 */
@Configuration
@ConditionalOnClass(WxPayService.class)
public class WxPayConfiguration {

    @Bean
    public WxPayService wxPayService() {
        return new WxPayServiceImpl();
    }


}
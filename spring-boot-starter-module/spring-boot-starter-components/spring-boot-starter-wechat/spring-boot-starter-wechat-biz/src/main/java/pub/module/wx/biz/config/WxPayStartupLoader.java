package pub.module.wx.biz.config;

import com.github.binarywang.wxpay.service.WxPayService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 应用启动后从数据库初始化 WxPay 多商户配置。
 */
@Slf4j
@Component
@Order
@ConditionalOnClass(WxPayService.class)
public class WxPayStartupLoader implements ApplicationRunner {

    @Resource
    private WxPayRuntimeRefresher wxPayRuntimeRefresher;

    @Override
    public void run(ApplicationArguments args) {
        try {
            wxPayRuntimeRefresher.refreshFromDatabase();
        }
        catch (Exception e) {
            log.warn("启动时加载 wx_pay_config 失败（表可能尚未创建）：{}", e.getMessage());
        }
    }
}

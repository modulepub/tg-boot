package pub.module.trade.biz.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.github.binarywang.wxpay.service.WxPayService;

/**
 * 应用启动后从数据库初始化 WxPay 多商户配置。
 *
 * @author tg
 * @since 2026-05-08
 */
@Slf4j
@Component
@Order
@ConditionalOnClass(WxPayService.class)
public class TradeWxPayStartupLoader implements ApplicationRunner {

    @Resource
    private TradeWxPayRuntimeRefresher tradeWxPayRuntimeRefresher;

    @Override
    public void run(ApplicationArguments args) {
        try {
            tradeWxPayRuntimeRefresher.refreshFromDatabase();
        } catch (Exception e) {
            log.warn("启动时加载微信支付配置失败（表可能尚未创建）：{}", e.getMessage());
        }
    }
}

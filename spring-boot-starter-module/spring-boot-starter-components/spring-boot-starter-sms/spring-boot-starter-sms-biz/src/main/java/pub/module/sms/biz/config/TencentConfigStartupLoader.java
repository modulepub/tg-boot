package pub.module.sms.biz.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 应用启动后从 sms_tencent_config 初始化腾讯云短信运行时配置。
 */
@Slf4j
@Component
@Order
public class TencentConfigStartupLoader implements ApplicationRunner {

    @Resource
    private TencentConfigRuntimeRefresher tencentConfigRuntimeRefresher;

    @Override
    public void run(ApplicationArguments args) {
        try {
            tencentConfigRuntimeRefresher.refreshFromDatabase();
        }
        catch (Exception e) {
            log.warn("启动时加载 sms_tencent_config 失败（表可能尚未创建）：{}", e.getMessage());
        }
    }
}

package pub.module.verification.biz.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 应用启动后从 vt_np_config 初始化二要素运行时配置。
 */
@Slf4j
@Component
@Order
public class NpConfigStartupLoader implements ApplicationRunner {

    @Resource
    private NpConfigRuntimeRefresher npConfigRuntimeRefresher;

    @Override
    public void run(ApplicationArguments args) {
        try {
            npConfigRuntimeRefresher.refreshFromDatabase();
        }
        catch (Exception e) {
            log.warn("启动时加载 vt_np_config 失败（表可能尚未创建）：{}", e.getMessage());
        }
    }
}

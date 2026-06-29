package pub.module.wx.biz.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 应用启动后从数据库初始化 WxMp 多公众号配置。
 */
@Slf4j
@Component
@Order
public class WxMpStartupLoader implements ApplicationRunner {

    @Resource
    private WxMpRuntimeRefresher wxMpRuntimeRefresher;

    @Override
    public void run(ApplicationArguments args) {
        try {
            wxMpRuntimeRefresher.refreshFromDatabase();
        }
        catch (Exception e) {
            log.warn("启动时加载 wx_mp_config 失败（表可能尚未创建）：{}", e.getMessage());
        }
    }
}

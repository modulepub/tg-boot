package pub.module.dating.biz.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pub.module.dating.biz.service.InitGoodsService;
import pub.module.trade.api.dto.TdGoodsDTO;

import java.util.List;

/**
 * 应用启动后初始化平台会员套餐商品（不存在则写入）。
 */
@Slf4j
@Component
@Order
public class DatingMemberGoodsStartupLoader implements ApplicationRunner {

    @Resource
    private InitGoodsService initGoodsService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<TdGoodsDTO> created = initGoodsService.initMemberGoodsIfAbsent();
            if (created.isEmpty()) {
                log.info("会员套餐商品已存在，跳过初始化");
                return;
            }
            log.info("启动时已初始化 {} 条会员套餐商品", created.size());
        }
        catch (Exception e) {
            log.warn("启动时初始化会员套餐商品失败（表可能尚未创建）：{}", e.getMessage());
        }
    }
}

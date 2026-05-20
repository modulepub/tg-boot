package pub.module.distribution.biz.job;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pub.module.distribution.api.service.ApiDistCommissionService;

@Component
@Slf4j
public class DistSettleScheduledJob {

    @Resource
    private ApiDistCommissionService apiDistCommissionService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void settleDueServicePeriods() {
        int count = apiDistCommissionService.settleDueServicePeriods();
        if (count > 0) {
            log.info("分销服务期结算完成，笔数={}", count);
        }
    }
}

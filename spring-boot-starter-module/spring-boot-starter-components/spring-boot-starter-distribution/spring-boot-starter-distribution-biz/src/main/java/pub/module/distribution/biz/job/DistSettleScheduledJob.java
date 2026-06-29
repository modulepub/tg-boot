package pub.module.distribution.biz.job;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pub.module.distribution.api.service.ApiDistUserBillSummaryService;

@Component
@Slf4j
public class DistSettleScheduledJob {

    @Resource
    private ApiDistUserBillSummaryService apiDistUserBillSummaryService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void settleDueServicePeriods() {
        int billCount = apiDistUserBillSummaryService.settleDueBillRecords();
        if (billCount > 0) {
            log.info("分销服务期结算完成，账单笔数={}", billCount);
        }
    }
}

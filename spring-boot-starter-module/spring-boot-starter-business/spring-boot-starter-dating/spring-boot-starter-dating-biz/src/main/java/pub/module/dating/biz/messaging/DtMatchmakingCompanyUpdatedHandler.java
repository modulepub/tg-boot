package pub.module.dating.biz.messaging;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.dating.api.messaging.DtMatchmakingCompanyUpdatedConsumer;
import pub.module.dating.api.messaging.DtMatchmakingCompanyUpdatedMessage;
import pub.module.dating.api.service.ApiMatchmakingCompanyRedundantSyncService;
import pub.module.dating.api.service.dto.MatchmakingCompanyRedundantDTO;

/**
 * 订阅婚介公司资料更新，同步婚恋模块冗余快照字段。
 */
@Slf4j
@Component
public class DtMatchmakingCompanyUpdatedHandler implements DtMatchmakingCompanyUpdatedConsumer.RedundantSync {

    @Override
    public void onCompanyUpdated(DtMatchmakingCompanyUpdatedMessage message) {
        if (message == null || message.getCompanyDto() == null) {
            return;
        }
        MatchmakingCompanyRedundantDTO company = message.getCompanyDto();
        String companyCode = StrUtil.trim(company.getMkCompanyCode());
        log.info("收到 dating.matchmaking-company.updated mkCompanyCode={}", companyCode);
        if (StrUtil.isBlank(companyCode)) {
            log.warn("跳过红娘表企业冗余同步：mkCompanyCode 为空");
            return;
        }
        try {
            SpringUtil.getBean(ApiMatchmakingCompanyRedundantSyncService.class).syncAfterCompanyUpdated(company);
        } catch (Exception ex) {
            log.warn("同步红娘表企业冗余字段失败 mkCompanyCode={}: {}", companyCode, ex.getMessage(), ex);
            throw ex;
        }
    }
}

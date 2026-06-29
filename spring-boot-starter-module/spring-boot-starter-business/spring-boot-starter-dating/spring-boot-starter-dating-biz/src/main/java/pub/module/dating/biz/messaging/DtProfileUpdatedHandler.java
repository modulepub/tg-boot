package pub.module.dating.biz.messaging;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.dating.api.messaging.DtProfileUpdatedConsumer;
import pub.module.dating.api.messaging.DtProfileUpdatedMessage;
import pub.module.dating.api.service.ApiCustomerRedundantSyncService;
import pub.module.dating.api.service.dto.DtCustomerDTO;

/**
 * 订阅客户资料更新，同步婚恋模块冗余快照字段。
 */
@Slf4j
@Component
public class DtProfileUpdatedHandler implements DtProfileUpdatedConsumer.RedundantSync {

    @Override
    public void onProfileUpdated(DtProfileUpdatedMessage message) {
        if (message == null || message.getCustomerDto() == null) {
            return;
        }
        DtCustomerDTO customer = message.getCustomerDto();
        String userCode = StrUtil.firstNonBlank(
                StrUtil.trim(message.getUserCode()),
                StrUtil.trim(customer.getCusUserCode()));
        log.info("收到 dating.profile.updated userCode={} cusCode={}",
                userCode, customer.getCusCode());
        if (StrUtil.isBlank(userCode)) {
            log.warn("跳过交友冗余同步：userCode 为空 cusCode={}", customer.getCusCode());
            return;
        }
        try {
            SpringUtil.getBean(ApiCustomerRedundantSyncService.class).syncAfterProfileUpdated(userCode, customer);
        } catch (Exception ex) {
            log.warn("同步交友模块客户冗余字段失败 userCode={} cusCode={}: {}",
                    userCode, customer.getCusCode(), ex.getMessage(), ex);
            throw ex;
        }
    }
}

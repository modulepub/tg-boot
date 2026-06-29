package pub.module.dating.biz.messaging;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.common.messaging.MqPublisher;
import pub.module.dating.api.messaging.DtMatchmakingCompanyUpdatedConsumer;
import pub.module.dating.api.messaging.DtMatchmakingCompanyUpdatedMessage;

/**
 * 婚介公司资料更新消息发布（事务提交后发送）。
 */
@Slf4j
@Service
public class DtMatchmakingCompanyUpdatedPublisher {

    @Resource
    private MqPublisher mqPublisher;

    public void publishAfterCommit(DtMatchmakingCompanyUpdatedMessage message) {
        if (message == null || message.getCompanyDto() == null) {
            return;
        }
        mqPublisher.publishAfterCommit(DtMatchmakingCompanyUpdatedConsumer.class, message);
        log.info("婚介公司资料更新消息已登记 destination={} mkCompanyCode={}",
                DtMatchmakingCompanyUpdatedConsumer.DESTINATION,
                message.getCompanyDto().getMkCompanyCode());
    }
}

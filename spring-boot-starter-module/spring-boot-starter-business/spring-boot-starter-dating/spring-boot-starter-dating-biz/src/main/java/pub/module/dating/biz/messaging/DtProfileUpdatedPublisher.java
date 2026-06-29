package pub.module.dating.biz.messaging;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.common.messaging.MqPublisher;
import pub.module.dating.api.messaging.DtProfileUpdatedConsumer;
import pub.module.dating.api.messaging.DtProfileUpdatedMessage;

/**
 * 客户资料更新消息发布（事务提交后发送，替代 Spring 本地事件）。
 */
@Slf4j
@Service
public class DtProfileUpdatedPublisher {

    @Resource
    private MqPublisher mqPublisher;

    public void publishAfterCommit(DtProfileUpdatedMessage message) {
        if (message == null || message.getCustomerDto() == null) {
            return;
        }
        mqPublisher.publishAfterCommit(DtProfileUpdatedConsumer.class, message);
        log.info("客户资料更新消息已登记 destination={} userCode={} cusCode={}",
                DtProfileUpdatedConsumer.DESTINATION,
                message.getUserCode(), message.getCustomerDto().getCusCode());
    }
}

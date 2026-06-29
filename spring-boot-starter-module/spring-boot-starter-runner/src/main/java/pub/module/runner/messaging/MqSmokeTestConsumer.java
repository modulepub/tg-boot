package pub.module.runner.messaging;

import pub.module.common.messaging.MqChannel;
import pub.module.common.messaging.MqMessageConsumer;
import pub.module.common.messaging.MqSubscribe;

/**
 * MQ 冒烟测试契约（发布 + 订阅同一 channel，仅用于验证链路）。
 */
@MqChannel(
        destination = MqSmokeTestConsumer.DESTINATION,
        producerFunction = MqSmokeTestConsumer.PRODUCER_FUNCTION
)
public interface MqSmokeTestConsumer extends MqMessageConsumer<MqSmokeTestMessage> {

    String DESTINATION = "runner.mq.smoke.test";
    String PRODUCER_FUNCTION = "runnerMqSmokeTest";

    void onMessage(MqSmokeTestMessage message);

    @MqSubscribe(group = "runner", function = "runnerMqSmokeTestListener")
    interface Runner extends MqSmokeTestConsumer {
    }
}

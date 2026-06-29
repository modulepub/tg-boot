package pub.module.runner.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * MQ 冒烟测试订阅者：收到消息即打印日志。
 */
@Slf4j
@Component
public class MqSmokeTestHandler implements MqSmokeTestConsumer.Runner {

    @Override
    public void onMessage(MqSmokeTestMessage message) {
        log.info("[MQ-SMOKE] 收到消息 text={} timestamp={}", message.text(), message.timestamp());
    }
}

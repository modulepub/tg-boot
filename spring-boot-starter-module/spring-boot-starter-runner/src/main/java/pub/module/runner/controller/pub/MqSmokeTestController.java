package pub.module.runner.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.messaging.MqPublisher;
import pub.module.runner.messaging.MqSmokeTestConsumer;
import pub.module.runner.messaging.MqSmokeTestMessage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MQ 冒烟测试：GET 发布消息，由 {@link pub.module.runner.messaging.MqSmokeTestHandler} 订阅并打日志。
 */
@RestController
@RequestMapping("/pub/mq/smoke")
@RequiredArgsConstructor
public class MqSmokeTestController {

    private final MqPublisher mqPublisher;

    @GetMapping("/publish")
    public Map<String, Object> publish(@RequestParam(value = "text", defaultValue = "hello-mq") String text) {
        MqSmokeTestMessage message = new MqSmokeTestMessage(text, System.currentTimeMillis());
        boolean sent = mqPublisher.publish(MqSmokeTestConsumer.class, message);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sent", sent);
        result.put("destination", MqSmokeTestConsumer.DESTINATION);
        result.put("payload", message);
        return result;
    }
}

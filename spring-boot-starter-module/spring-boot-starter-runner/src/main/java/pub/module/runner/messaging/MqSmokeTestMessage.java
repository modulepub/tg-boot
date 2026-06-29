package pub.module.runner.messaging;

/**
 * MQ 冒烟测试消息体。
 */
public record MqSmokeTestMessage(String text, long timestamp) {
}

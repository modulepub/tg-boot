package pub.module.system.api.messaging;

import pub.module.common.messaging.MqChannel;
import pub.module.common.messaging.MqMessageConsumer;
import pub.module.common.messaging.MqSubscribe;

/**
 * 新用户注册成功 — MQ 全链路契约。
 */
@MqChannel(
        destination = SystemUserRegisteredConsumer.DESTINATION,
        producerFunction = SystemUserRegisteredConsumer.PRODUCER_FUNCTION
)
public interface SystemUserRegisteredConsumer extends MqMessageConsumer<SysUserRegisteredMessage> {

    String DESTINATION = "system.user.registered";
    String PRODUCER_FUNCTION = "systemUserRegistered";

    void onUserRegistered(SysUserRegisteredMessage message);

    @MqSubscribe(group = "distribution", function = "distSystemUserRegistered")
    interface Distribution extends SystemUserRegisteredConsumer {
    }

    @MqSubscribe(group = "dating", function = "datingSystemUserRegistered")
    interface Dating extends SystemUserRegisteredConsumer {
    }
}

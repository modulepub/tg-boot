package pub.module.system.api.messaging;

import pub.module.common.messaging.MqChannel;
import pub.module.common.messaging.MqMessageConsumer;
import pub.module.common.messaging.MqSubscribe;

/**
 * 用户信息更新 — MQ 全链路契约。
 */
@MqChannel(
        destination = SystemUserInfoUpdatedConsumer.DESTINATION,
        producerFunction = SystemUserInfoUpdatedConsumer.PRODUCER_FUNCTION
)
public interface SystemUserInfoUpdatedConsumer extends MqMessageConsumer<SysUserInfoUpdatedMessage> {

    String DESTINATION = "system.user-info.updated";
    String PRODUCER_FUNCTION = "systemUserInfoUpdated";

    void onUserInfoUpdated(SysUserInfoUpdatedMessage message);

    @MqSubscribe(group = "dating", function = "datingSystemUserInfoUpdated")
    interface Dating extends SystemUserInfoUpdatedConsumer {
    }
}

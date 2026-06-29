package pub.module.system.api.messaging;

import pub.module.common.messaging.MqChannel;
import pub.module.common.messaging.MqMessageConsumer;
import pub.module.common.messaging.MqSubscribe;

/**
 * 用户登录成功 — MQ 全链路契约。
 */
@MqChannel(
        destination = SystemUserLoginConsumer.DESTINATION,
        producerFunction = SystemUserLoginConsumer.PRODUCER_FUNCTION
)
public interface SystemUserLoginConsumer extends MqMessageConsumer<SysUserLoginMessage> {

    String DESTINATION = "system.user.login";
    String PRODUCER_FUNCTION = "systemUserLogin";

    void onUserLogin(SysUserLoginMessage message);

    @MqSubscribe(group = "dating", function = "datingSystemUserLogin")
    interface Dating extends SystemUserLoginConsumer {
    }
}

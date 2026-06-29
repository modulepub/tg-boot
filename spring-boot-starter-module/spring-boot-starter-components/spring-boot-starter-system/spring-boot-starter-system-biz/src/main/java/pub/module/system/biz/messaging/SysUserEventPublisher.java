package pub.module.system.biz.messaging;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.common.messaging.MqPublisher;
import pub.module.system.api.messaging.SysUserInfoUpdatedMessage;
import pub.module.system.api.messaging.SysUserLoginMessage;
import pub.module.system.api.messaging.SysUserRegisteredMessage;
import pub.module.system.api.messaging.SystemUserInfoUpdatedConsumer;
import pub.module.system.api.messaging.SystemUserLoginConsumer;
import pub.module.system.api.messaging.SystemUserRegisteredConsumer;
import pub.module.system.api.service.dto.UserDTO;

/**
 * 系统用户事件 MQ 发布（替代 Spring 本地 ApplicationEvent）。
 */
@Slf4j
@Service
public class SysUserEventPublisher {

    @Resource
    private MqPublisher mqPublisher;

    public void publishUserLogin(UserDTO user) {
        if (user == null) {
            return;
        }
        mqPublisher.publishAfterCommit(SystemUserLoginConsumer.class, new SysUserLoginMessage(user));
    }

    public void publishUserInfoUpdatedAfterCommit(UserDTO user) {
        if (user == null) {
            return;
        }
        mqPublisher.publishAfterCommit(SystemUserInfoUpdatedConsumer.class, new SysUserInfoUpdatedMessage(user));
    }

    public void publishUserRegisteredAfterCommit(UserDTO user, String userReferenceUserCode) {
        if (user == null) {
            return;
        }
        mqPublisher.publishAfterCommit(
                SystemUserRegisteredConsumer.class,
                new SysUserRegisteredMessage(user, userReferenceUserCode));
    }
}

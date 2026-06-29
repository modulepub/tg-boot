package pub.module.dating.biz.messaging;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.system.api.messaging.SystemUserInfoUpdatedConsumer;
import pub.module.system.api.messaging.SysUserInfoUpdatedMessage;
import pub.module.system.api.service.dto.UserDTO;

/**
 * 订阅系统用户信息更新，同步客户昵称。
 */
@Slf4j
@Component
public class DatingSystemUserInfoUpdatedHandler implements SystemUserInfoUpdatedConsumer.Dating {

    @Resource
    private ApiDtCustomerService apiDtCustomerService;

    @Override
    public void onUserInfoUpdated(SysUserInfoUpdatedMessage message) {
        if (message == null || message.getUser() == null) {
            return;
        }
        UserDTO user = message.getUser();
        String userCode = user.getUserCode() == null ? "" : user.getUserCode().trim();
        String nickName = user.getUserNickName() == null ? "" : user.getUserNickName().trim();
        if (userCode.isEmpty() || nickName.isEmpty()) {
            return;
        }
        try {
            apiDtCustomerService.syncCusNickNameFromUser(user);
        } catch (Exception ex) {
            log.warn("同步客户昵称失败 userCode={}: {}", user.getUserCode(), ex.getMessage());
            throw ex;
        }
    }
}

package pub.module.dating.biz.messaging;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.system.api.messaging.SystemUserInfoUpdatedConsumer;
import pub.module.system.api.messaging.SystemUserLoginConsumer;
import pub.module.system.api.messaging.SysUserInfoUpdatedMessage;
import pub.module.system.api.messaging.SysUserLoginMessage;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyResult;
import pub.module.verification.api.messaging.PhoneTwoFactorVerifiedConsumer;
import pub.module.verification.api.messaging.PhoneTwoFactorVerifiedMessage;

/**
 * 订阅系统用户登录消息，初始化客户档案。
 */
@Slf4j
@Component
public class DatingSystemUserLoginHandler implements SystemUserLoginConsumer.Dating {

    @Resource
    private ApiDtCustomerService apiDtCustomerService;

    @Override
    public void onUserLogin(SysUserLoginMessage message) {
        if (message == null || message.getUser() == null) {
            return;
        }
        UserDTO user = message.getUser();
        try {
            apiDtCustomerService.initCustomerByUser(user);
        } catch (Exception ex) {
            log.warn("登录后初始化客户失败 userCode={}: {}", user.getUserCode(), ex.getMessage());
            throw ex;
        }
    }
}

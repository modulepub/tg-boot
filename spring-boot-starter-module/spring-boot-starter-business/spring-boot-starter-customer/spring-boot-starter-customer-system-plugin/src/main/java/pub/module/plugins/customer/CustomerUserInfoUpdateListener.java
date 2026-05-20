package pub.module.plugins.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.system.api.event.SysUserInfoUpdatedEvent;
import pub.module.system.api.service.dto.UserDTO;

/**
 * 用户信息更新后，将昵称同步到客户表 {@code cusNickName}。
 */
public class CustomerUserInfoUpdateListener {

    private static final Logger log = LoggerFactory.getLogger(CustomerUserInfoUpdateListener.class);

    private final ApiCustomerService apiCustomerService;

    public CustomerUserInfoUpdateListener(ApiCustomerService apiCustomerService) {
        this.apiCustomerService = apiCustomerService;
    }

    @Order(100)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserInfoUpdated(SysUserInfoUpdatedEvent event) {
        if (event == null || event.getUser() == null) {
            return;
        }
        UserDTO user = event.getUser();
        String userCode = user.getUserCode() == null ? "" : user.getUserCode().trim();
        String nickName = user.getUserNickName() == null ? "" : user.getUserNickName().trim();
        if (userCode.isEmpty() || nickName.isEmpty()) {
            return;
        }
        try {
            apiCustomerService.syncCusNickNameFromUser(user);
        }
        catch (Exception ex) {
            log.warn("同步客户昵称失败 userCode={}: {}", user.getUserCode(), ex.getMessage());
        }
    }
}

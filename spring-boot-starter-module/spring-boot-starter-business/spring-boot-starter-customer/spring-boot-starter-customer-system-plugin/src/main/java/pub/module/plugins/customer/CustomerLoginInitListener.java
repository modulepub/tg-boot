package pub.module.plugins.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.system.api.event.SysUserLoginEvent;

/**
 * 登录成功后幂等初始化客户表记录。
 */
public class CustomerLoginInitListener {

    private static final Logger log = LoggerFactory.getLogger(CustomerLoginInitListener.class);

    private final ApiCustomerService apiCustomerService;

    public CustomerLoginInitListener(ApiCustomerService apiCustomerService) {
        this.apiCustomerService = apiCustomerService;
    }

    @Order(100)
    @EventListener
    public void onUserLogin(SysUserLoginEvent event) {
        if (event == null || event.getUser() == null) {
            return;
        }
        try {
            apiCustomerService.initCustomerByUser(event.getUser());
        } catch (Exception ex) {
            log.warn("登录后初始化客户失败 userCode={}: {}", event.getUser().getUserCode(), ex.getMessage());
        }
    }
}

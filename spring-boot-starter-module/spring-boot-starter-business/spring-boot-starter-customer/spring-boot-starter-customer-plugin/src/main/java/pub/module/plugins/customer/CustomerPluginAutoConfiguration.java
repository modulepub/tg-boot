package pub.module.plugins.customer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.system.api.event.SysUserLoginEvent;

/**
 * 客户插件自动装配：登录后初始化客户数据（依赖宿主已注册 {@link ApiCustomerService}）。
 */
@AutoConfiguration
@ConditionalOnClass({ApiCustomerService.class, SysUserLoginEvent.class})
@ConditionalOnBean(ApiCustomerService.class)
public class CustomerPluginAutoConfiguration {

    @Bean
    CustomerLoginInitListener customerLoginInitListener(ApiCustomerService apiCustomerService) {
        return new CustomerLoginInitListener(apiCustomerService);
    }
}

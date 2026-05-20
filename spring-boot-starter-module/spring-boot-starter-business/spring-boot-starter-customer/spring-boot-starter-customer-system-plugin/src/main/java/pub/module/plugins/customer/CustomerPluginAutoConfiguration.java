package pub.module.plugins.customer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.system.api.event.SysUserInfoUpdatedEvent;
import pub.module.system.api.event.SysUserLoginEvent;

/**
 * 客户插件自动装配：登录初始化、用户信息变更同步等（依赖宿主已注册 {@link ApiCustomerService}）。
 */
@AutoConfiguration
@ConditionalOnClass(ApiCustomerService.class)
@ConditionalOnBean(ApiCustomerService.class)
public class CustomerPluginAutoConfiguration {

    @Bean
    @ConditionalOnClass(SysUserLoginEvent.class)
    CustomerLoginInitListener customerLoginInitListener(ApiCustomerService apiCustomerService) {
        return new CustomerLoginInitListener(apiCustomerService);
    }

    @Bean
    @ConditionalOnClass(SysUserInfoUpdatedEvent.class)
    CustomerUserInfoUpdateListener customerUserInfoUpdateListener(ApiCustomerService apiCustomerService) {
        return new CustomerUserInfoUpdateListener(apiCustomerService);
    }
}

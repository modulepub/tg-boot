package pub.module.plugins.customer.verification;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.verification.api.service.SpiPhoneTwoFactorVerifyNotify;

/**
 * 客户-核验插件：注册二要素核验通过后的实名回调（依赖宿主已加载 verification 与客户能力）。
 */
@AutoConfiguration
@ConditionalOnClass({SpiPhoneTwoFactorVerifyNotify.class, ApiCustomerService.class})
@ConditionalOnBean(ApiCustomerService.class)
public class CustomerVerificationPluginAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CustomerPhoneTwoFactorVerifyNotify.class)
    CustomerPhoneTwoFactorVerifyNotify customerPhoneTwoFactorVerifyNotify(ApiCustomerService apiCustomerService) {
        return new CustomerPhoneTwoFactorVerifyNotify(apiCustomerService);
    }
}

package pub.module.plugins.dating.distribution;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pub.module.dating.api.service.ApiDtMatchmakerService;
import pub.module.distribution.api.service.SpiDistPromoterRoleResolver;

@Configuration
@ConditionalOnClass({SpiDistPromoterRoleResolver.class, ApiDtMatchmakerService.class})
@ComponentScan(basePackages = "pub.module.plugins.dating.distribution")
public class DatingDistributionPluginAutoConfiguration {
}

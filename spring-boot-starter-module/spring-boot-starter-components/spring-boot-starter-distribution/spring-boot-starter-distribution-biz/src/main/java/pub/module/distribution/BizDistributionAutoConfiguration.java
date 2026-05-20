package pub.module.distribution;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages = {"pub.module.distribution.**"})
@MapperScan(basePackages = {"pub.module.distribution.**.mapper"})
@EnableScheduling
public class BizDistributionAutoConfiguration {
}

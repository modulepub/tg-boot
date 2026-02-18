package pub.module.customer;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ComponentScan(basePackages = {"pub.module.customer.**"})
@MapperScan(basePackages = {"pub.module.customer.**.mapper"})
public class CustomerAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public CustomerAutoConfiguration init() {
        return new CustomerAutoConfiguration();
    }
}

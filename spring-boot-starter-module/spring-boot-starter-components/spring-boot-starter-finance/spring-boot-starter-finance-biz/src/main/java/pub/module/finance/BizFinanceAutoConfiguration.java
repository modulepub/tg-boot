package pub.module.finance;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@ComponentScan(basePackages = {"pub.module.finance.**"})
@MapperScan(basePackages = {"pub.module.finance.**.mapper"})
@Configuration
public class BizFinanceAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public BizFinanceAutoConfiguration init() {
        return new BizFinanceAutoConfiguration();
    }
}

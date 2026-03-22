package pub.module.data;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ComponentScan(basePackages = {"pub.module.data.**"})
public class BizDataAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public BizDataAutoConfiguration init() {
        return new BizDataAutoConfiguration();
    }
}

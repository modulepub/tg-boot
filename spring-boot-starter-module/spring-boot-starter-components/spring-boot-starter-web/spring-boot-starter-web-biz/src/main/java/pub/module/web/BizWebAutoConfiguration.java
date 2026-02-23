package pub.module.web;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@ComponentScan(basePackages = {"pub.module.web.**"})
@Configuration
public class BizWebAutoConfiguration {



    @Bean
    @ConditionalOnMissingBean
    public BizWebAutoConfiguration init() {
        return new BizWebAutoConfiguration();
    }
}

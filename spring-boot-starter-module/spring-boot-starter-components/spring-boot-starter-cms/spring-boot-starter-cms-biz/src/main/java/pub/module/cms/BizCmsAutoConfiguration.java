package pub.module.cms;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ComponentScan(basePackages = {"pub.module.cms.**"})
@MapperScan(basePackages = {"pub.module.cms.**.mapper"})
public class BizCmsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public BizCmsAutoConfiguration init() {
        return new BizCmsAutoConfiguration();
    }
}

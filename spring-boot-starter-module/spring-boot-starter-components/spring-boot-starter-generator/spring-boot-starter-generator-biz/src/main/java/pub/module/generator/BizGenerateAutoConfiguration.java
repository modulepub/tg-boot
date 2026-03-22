package pub.module.generator;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ComponentScan(basePackages = {"pub.module.generator.**"})
@MapperScan(basePackages = {"pub.module.generator.**.mapper"})
public class BizGenerateAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public BizGenerateAutoConfiguration init() {
        return new BizGenerateAutoConfiguration();
    }
}

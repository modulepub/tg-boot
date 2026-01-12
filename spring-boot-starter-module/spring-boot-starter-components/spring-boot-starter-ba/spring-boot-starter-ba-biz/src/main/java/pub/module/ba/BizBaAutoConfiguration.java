package pub.module.ba;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@ComponentScan(basePackages = {"pub.module.ba.**"})
@MapperScan(basePackages = {"pub.module.ba.**.mapper"})
@Configuration
public class BizBaAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public BizBaAutoConfiguration init() {
        return new BizBaAutoConfiguration();
    }
}

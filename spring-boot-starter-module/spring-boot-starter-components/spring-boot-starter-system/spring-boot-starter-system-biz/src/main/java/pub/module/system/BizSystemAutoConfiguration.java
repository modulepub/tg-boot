package pub.module.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {"pub.module.system.**", "cn.hutool.extra.spring"})
@MapperScan(basePackages = {"pub.module.system.**.mapper"})
@Configuration
public class BizSystemAutoConfiguration {



    @Bean
    @ConditionalOnMissingBean
    public BizSystemAutoConfiguration init() {
        return new BizSystemAutoConfiguration();
    }
}

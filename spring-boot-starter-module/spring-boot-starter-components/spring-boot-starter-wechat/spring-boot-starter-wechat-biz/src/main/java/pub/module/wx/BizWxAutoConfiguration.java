package pub.module.wx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {"pub.module.wx.**"})
@MapperScan(basePackages = {"pub.module.wx.crud.mapper"})
@Configuration
public class BizWxAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public BizWxAutoConfiguration init() {
        return new BizWxAutoConfiguration();
    }
}

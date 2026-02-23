package pub.module.wx.biz;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@ComponentScan(basePackages = {"pub.module.wx.**"})
@Configuration
public class BizWxAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public BizWxAutoConfiguration init() {
        return new BizWxAutoConfiguration();
    }
}

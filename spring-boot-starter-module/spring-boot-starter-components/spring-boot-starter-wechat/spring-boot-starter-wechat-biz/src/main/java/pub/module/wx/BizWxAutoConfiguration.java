package pub.module.wx;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 微信模块自动配置类
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
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

package pub.module.web.biz;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Web模块自动配置类
 * 配置Web模块的组件扫描
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
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

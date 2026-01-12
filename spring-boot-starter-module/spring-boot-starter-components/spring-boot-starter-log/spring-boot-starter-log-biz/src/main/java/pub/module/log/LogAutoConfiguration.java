package pub.module.log;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Security 自动配置类
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = {"pub.module.log.**"})
@MapperScan(basePackages = {"pub.module.log.**.mapper"})
public class LogAutoConfiguration {
    




}


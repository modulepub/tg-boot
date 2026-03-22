package pub.module.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan(basePackages = {"pub.module.config.**"})
@MapperScan(basePackages = {"pub.module.config.**.mapper"})
public class BizConfigAutoConfiguration {
    




}

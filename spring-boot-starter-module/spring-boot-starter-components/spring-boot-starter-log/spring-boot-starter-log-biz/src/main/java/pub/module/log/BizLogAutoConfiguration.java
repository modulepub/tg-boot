package pub.module.log;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan(basePackages = {"pub.module.log.**"})
@MapperScan(basePackages = {"pub.module.log.**.mapper"})
public class BizLogAutoConfiguration {
    




}

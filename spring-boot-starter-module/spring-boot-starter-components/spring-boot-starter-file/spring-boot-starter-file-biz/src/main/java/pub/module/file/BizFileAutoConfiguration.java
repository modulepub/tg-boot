package pub.module.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {"pub.module.file.**"})
@MapperScan(basePackages = {"pub.module.file.**.mapper"})
@Configuration
public class BizFileAutoConfiguration {

}

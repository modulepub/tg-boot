package pub.module.dict;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"pub.module.dict.**"})
@MapperScan(basePackages = {"pub.module.dict.**.mapper"})
public class BizDictAutoConfiguration {

}

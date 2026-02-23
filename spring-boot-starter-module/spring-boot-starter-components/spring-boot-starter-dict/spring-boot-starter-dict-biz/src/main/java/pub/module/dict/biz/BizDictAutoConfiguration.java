package pub.module.dict.biz;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ComponentScan(basePackages = {"pub.module.dict.**"})
@MapperScan(basePackages = {"pub.module.dict.**.mapper"})
public class BizDictAutoConfiguration {

}

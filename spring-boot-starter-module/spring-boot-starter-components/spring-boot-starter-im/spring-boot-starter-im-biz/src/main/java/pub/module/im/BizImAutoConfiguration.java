package pub.module.im;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@ComponentScan(basePackages = {"pub.module.im.**"})
@MapperScan(basePackages = {"pub.module.im.**.mapper"})
@Configuration
public class BizImAutoConfiguration {

}

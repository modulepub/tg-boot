package pub.module.dating;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@ComponentScan(basePackages = {"pub.module.dating.**"})
@MapperScan(basePackages = {"pub.module.dating.**.mapper"})
@Configuration
public class BizDatingConfiguration {
}

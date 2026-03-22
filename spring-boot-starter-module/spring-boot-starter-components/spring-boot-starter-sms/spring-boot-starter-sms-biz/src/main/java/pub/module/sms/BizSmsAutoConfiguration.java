package pub.module.sms;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@ComponentScan(basePackages = {"pub.module.sms.**"})
@Configuration
public class BizSmsAutoConfiguration {


}

package pub.module.verification;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"pub.module.verification.**"})
@MapperScan(basePackages = {"pub.module.verification.**.mapper"})
public class BizVerificationAutoConfiguration {
}

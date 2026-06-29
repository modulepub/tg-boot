package pub.module.verification;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration(beforeName = {
        "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration",
        "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration"
})
@ComponentScan(basePackages = {"pub.module.verification.**"})
@MapperScan(basePackages = {"pub.module.verification.crud.mapper"})
public class BizVerificationAutoConfiguration {
}

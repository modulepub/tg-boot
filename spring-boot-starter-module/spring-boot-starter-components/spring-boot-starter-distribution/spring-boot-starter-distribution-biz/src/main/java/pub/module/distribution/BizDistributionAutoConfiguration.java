package pub.module.distribution;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@AutoConfiguration(beforeName = {
        "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration",
        "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration"
})
@ComponentScan(basePackages = {"pub.module.distribution.**"})
@MapperScan(basePackages = {"pub.module.distribution.crud.mapper"})
@EnableScheduling
public class BizDistributionAutoConfiguration {
}

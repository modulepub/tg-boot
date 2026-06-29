package pub.module.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"pub.module.file.**"})
@MapperScan(basePackages = {"pub.module.file.crud.mapper"})
@AutoConfiguration(beforeName = {
        "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration",
        "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration"
})
public class BizFileAutoConfiguration {
}

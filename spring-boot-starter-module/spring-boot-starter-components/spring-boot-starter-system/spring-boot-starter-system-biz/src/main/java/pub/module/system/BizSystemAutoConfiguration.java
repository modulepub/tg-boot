package pub.module.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"pub.module.system.**", "cn.hutool.extra.spring"})
@MapperScan(basePackages = {"pub.module.system.crud.mapper"})
@AutoConfiguration(beforeName = {
        "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration",
        "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration"
})
public class BizSystemAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public BizSystemAutoConfiguration init() {
        return new BizSystemAutoConfiguration();
    }
}

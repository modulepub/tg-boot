package pub.module.cms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration(beforeName = {
        "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration",
        "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration"
})
@ComponentScan(basePackages = {"pub.module.cms.**"})
@MapperScan(basePackages = {"pub.module.cms.crud.mapper"})
public class BizCmsAutoConfiguration {    @Bean
    @ConditionalOnMissingBean
    public BizCmsAutoConfiguration init() {
        return new BizCmsAutoConfiguration();
    }
}

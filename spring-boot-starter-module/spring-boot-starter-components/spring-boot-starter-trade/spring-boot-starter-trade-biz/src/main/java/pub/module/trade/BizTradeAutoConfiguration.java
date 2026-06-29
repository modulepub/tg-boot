package pub.module.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"pub.module.trade.**"})
@MapperScan(basePackages = {"pub.module.trade.crud.mapper"})
@AutoConfiguration(beforeName = {
        "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration",
        "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration"
})
public class BizTradeAutoConfiguration {
}

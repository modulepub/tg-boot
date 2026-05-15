package pub.module.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = {"pub.module.trade.**"})
@MapperScan(basePackages = {"pub.module.trade.**.mapper"})
@Configuration
public class BizTradeAutoConfiguration {

}

package pub.module.trade.biz;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@ComponentScan(basePackages = {"pub.module.trade.**"})
@MapperScan(basePackages = {"pub.module.trade.**.mapper"})
@Configuration
public class BizTradeAutoConfiguration {

}

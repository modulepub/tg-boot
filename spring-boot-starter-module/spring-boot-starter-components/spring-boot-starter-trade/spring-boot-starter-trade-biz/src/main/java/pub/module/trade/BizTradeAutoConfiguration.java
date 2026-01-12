package pub.module.trade;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 交易模块自动配置类
 * 配置交易模块的组件扫描和Mapper扫描
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Data
@ComponentScan(basePackages = {"pub.module.trade.**"})
@MapperScan(basePackages = {"pub.module.trade.**.mapper"})
@Configuration
public class BizTradeAutoConfiguration {

}

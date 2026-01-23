package pub.module.contract;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackages = {"pub.module.contract.**"})
@MapperScan(basePackages = {"pub.module.contract.**.mapper"})
public class BizContractAutoConfiguration {

}

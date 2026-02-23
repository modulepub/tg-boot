package pub.module.cms.biz;

import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ComponentScan(basePackages = {"pub.module.cms.**"})
@MapperScan(basePackages = {"pub.module.cms.**.mapper"})
public class BizCmsAutoConfiguration {

}

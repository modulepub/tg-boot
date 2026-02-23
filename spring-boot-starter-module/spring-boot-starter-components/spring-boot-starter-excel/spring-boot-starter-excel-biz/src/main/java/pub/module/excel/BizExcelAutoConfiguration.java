package pub.module.excel;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Excel模块自动配置类
 * 配置Excel模块的组件扫描
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Slf4j
@Data
@Configuration
@ComponentScan(basePackages = {"pub.module.excel.**"})
public class BizExcelAutoConfiguration {

    public BizExcelAutoConfiguration() {
       log.info("已经加载配置类");
    }

}

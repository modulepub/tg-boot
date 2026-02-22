package pub.module.excel.biz;

import lombok.Data;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Excel模块自动配置类
 * 配置Excel模块的组件扫描
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Data
@Configuration
@ComponentScan(basePackages = {"pub.module.excel.biz.**"})
public class BizExcelAutoConfiguration {

    public BizExcelAutoConfiguration() {
        System.err.println("已经加载配置类");
    }

}

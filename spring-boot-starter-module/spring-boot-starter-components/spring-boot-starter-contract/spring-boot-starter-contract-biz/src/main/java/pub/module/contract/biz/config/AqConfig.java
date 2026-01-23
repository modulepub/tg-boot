package pub.module.contract.biz.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 读取代码生成相关配置
 *
 * @author pz
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aq")
public class AqConfig {
    /** 生成包路径 */
    private String url;
    private List<App> apps;


}
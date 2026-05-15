package pub.module.common.plugin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pub.module.common.plugin.bootstrap.ExternalPluginBootstrap;

/**
 * 与 {@link ExternalPluginBootstrap#resolveDirectory} 默认一致；
 * 启动扫描仍以 JVM 参数/环境变量为准，此配置用于文档与默认值对齐。
 */
@Data
@ConfigurationProperties(prefix = "tg.plugins")
public class TgPluginsProperties {

    /**
     * 外部插件 JAR 目录（相对于工作目录），默认 ./plugins
     */
    private String directory = "./plugins";
}

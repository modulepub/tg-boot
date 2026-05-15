package pub.module.common.plugin.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * 插件宿主侧自动配置（状态注册、查询接口；不含外部 JAR 本身）。
 */
@AutoConfiguration
@ComponentScan(basePackages = "pub.module.plugins.biz")
@EnableConfigurationProperties(TgPluginsProperties.class)
public class PluginsBizAutoConfiguration {
}

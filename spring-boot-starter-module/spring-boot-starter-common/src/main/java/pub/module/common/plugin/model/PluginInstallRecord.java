package pub.module.common.plugin.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 单个插件 JAR 的安装/加载情况（内存态）
 */
@Data
@Builder
public class PluginInstallRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String jarFileName;
    private String pluginCode;
    private String pluginName;
    private String pluginDescription;
    private PluginLoadState loadState;
    private String message;
    private String autoConfigurationClasses;
}

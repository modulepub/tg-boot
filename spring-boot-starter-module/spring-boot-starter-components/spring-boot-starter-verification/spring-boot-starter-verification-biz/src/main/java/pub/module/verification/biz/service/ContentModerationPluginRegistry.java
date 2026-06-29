package pub.module.verification.biz.service;

import cn.hutool.core.lang.Assert;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ContentModerationPluginRegistry {

    private final Map<String, SpiContentModerationPlugin> plugins;

    public ContentModerationPluginRegistry(List<SpiContentModerationPlugin> plugins) {
        this.plugins = plugins.stream()
                .collect(Collectors.toMap(SpiContentModerationPlugin::pluginCode, Function.identity(), (a, b) -> a));
    }

    public SpiContentModerationPlugin require(String pluginCode) {
        SpiContentModerationPlugin plugin = plugins.get(pluginCode);
        Assert.notNull(plugin, "未找到内容合法校验插件：{}", pluginCode);
        return plugin;
    }
}

package pub.module.plugins.trade;

import pub.module.common.plugin.spi.TgPlugin;

/**
 * 与 {@link TradeDatingPluginAutoConfiguration} 配套的 SPI 元数据，供管理端列出 classpath 插件。
 */
public class TradeDatingTgPlugin implements TgPlugin {

    @Override
    public String getPluginCode() {
        return "TradeDatingPluginAutoConfiguration";
    }

    @Override
    public String getPluginName() {
        return "交易系统-红娘系统插件";
    }

    @Override
    public String getPluginDescription() {
        return "支持交易系统特定商品交易处理";
    }
}

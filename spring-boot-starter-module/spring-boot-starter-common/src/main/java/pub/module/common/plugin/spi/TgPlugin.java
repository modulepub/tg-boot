package pub.module.common.plugin.spi;

/**
 * 外部插件 SPI：由插件 JAR 在 META-INF/services 下注册实现类。
 *
 * @author tg-boot
 */
public interface TgPlugin {

    /**
     * 插件唯一编码
     */
    String getPluginCode();

    /**
     * 展示名称
     */
    String getPluginName();

    /**
     * 描述
     */
    String getPluginDescription();

}

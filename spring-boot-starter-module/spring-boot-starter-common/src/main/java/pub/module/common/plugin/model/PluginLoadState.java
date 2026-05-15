package pub.module.common.plugin.model;

/**
 * 插件加载状态（内存记录）
 */
public enum PluginLoadState {
    /** 已成功注册 Spring 配置 */
    LOADED,
    /** 扫描或装载失败 */
    FAILED,
    /** JAR 可读但未发现 Boot AutoConfiguration 清单 */
    NO_AUTO_CONFIGURATION
}

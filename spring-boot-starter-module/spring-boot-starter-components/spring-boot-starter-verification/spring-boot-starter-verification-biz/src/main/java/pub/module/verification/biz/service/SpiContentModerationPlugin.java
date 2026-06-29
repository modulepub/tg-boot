package pub.module.verification.biz.service;

import pub.module.verification.api.dto.ContentModerationPluginOutcome;
import pub.module.verification.api.dto.ContentModerationRequest;

/**
 * 内容合法校验插件 SPI（模块内多实现，按 pluginCode 路由）
 */
public interface SpiContentModerationPlugin {

    String pluginCode();

    ContentModerationPluginOutcome check(ContentModerationRequest context, String contentTypeCode, String content);
}

package pub.module.wx.biz.config;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import pub.module.wx.crud.entity.WxMiniConfig;
import pub.module.wx.crud.service.WxMiniConfigService;

/**
 * 解析微信小程序 AppId：优先使用调用方传入值，否则取启用配置中的第一条。
 */
@Component
public class WxMaConfigResolver {

    private static final String ENABLED_CODE = "1";

    @Resource
    private WxMaRuntimeRefresher wxMaRuntimeRefresher;
    @Resource
    private WxMiniConfigService wxMiniConfigService;

    /**
     * 解析并返回可用的 AppId，必要时触发运行时配置加载。
     * <p>优先级：wxMiniConfigCode &gt; preferredAppId &gt; 第一条启用配置。</p>
     */
    public String resolveAppId(String wxMiniConfigCode, String preferredAppId) {
        wxMaRuntimeRefresher.ensureLoaded();
        String configCode = StrUtil.trimToNull(wxMiniConfigCode);
        if (configCode != null) {
            return resolveAppIdByConfigCode(configCode);
        }
        String appId = StrUtil.trimToNull(preferredAppId);
        if (appId != null) {
            return appId;
        }
        return resolveDefaultEnabledAppId();
    }

    private String resolveAppIdByConfigCode(String wxMiniConfigCode) {
        WxMiniConfig config = wxMiniConfigService.getByCode(wxMiniConfigCode);
        Assert.notNull(config, "微信小程序配置不存在 wx_mini_config_code=" + wxMiniConfigCode);
        Assert.isTrue(ENABLED_CODE.equals(StrUtil.trim(config.getWxMiniConfigEnabledCode())),
                "微信小程序配置未启用 wx_mini_config_code=" + wxMiniConfigCode);
        Assert.notBlank(config.getWxMiniConfigAppId(),
                "微信小程序配置缺少 AppId wx_mini_config_code=" + wxMiniConfigCode);
        return config.getWxMiniConfigAppId().trim();
    }

    private String resolveDefaultEnabledAppId() {
        WxMiniConfig config = wxMiniConfigService.lambdaQuery()
                .eq(WxMiniConfig::getWxMiniConfigEnabledCode, ENABLED_CODE)
                .orderByAsc(WxMiniConfig::getSeqNo)
                .orderByAsc(WxMiniConfig::getWxMiniConfigCode)
                .last("LIMIT 1")
                .one();
        Assert.notNull(config, "未找到启用的小程序配置，请先在后台维护 wx_mini_config");
        Assert.notBlank(config.getWxMiniConfigAppId(), "启用的小程序配置缺少 AppId");
        return config.getWxMiniConfigAppId().trim();
    }
}

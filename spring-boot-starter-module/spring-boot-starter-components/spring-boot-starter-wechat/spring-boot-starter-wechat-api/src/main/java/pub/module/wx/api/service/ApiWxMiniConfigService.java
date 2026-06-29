package pub.module.wx.api.service;

import pub.module.wx.api.dto.WxMiniConfigDTO;

import java.util.Collection;

/**
 * 微信小程序配置业务服务（增删改后刷新运行时）。
 */
public interface ApiWxMiniConfigService {

    void addAndRefreshRuntime(WxMiniConfigDTO dto);

    void updateAndRefreshRuntime(WxMiniConfigDTO dto);

    void removeAndRefreshRuntime(Collection<String> wxMiniConfigCodes);

    void refreshWxMaRuntimeFromDatabase();
}

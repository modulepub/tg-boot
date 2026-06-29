package pub.module.wx.api.service;

import pub.module.wx.api.dto.WxMpConfigDTO;
import pub.module.wx.api.dto.WxMpMenuDTO;

import java.util.Collection;

/**
 * 微信公众号配置业务服务（增删改后刷新运行时）。
 */
public interface ApiWxMpConfigService {

    void addAndRefreshRuntime(WxMpConfigDTO dto);

    void updateAndRefreshRuntime(WxMpConfigDTO dto);

    void removeAndRefreshRuntime(Collection<String> wxMpConfigCodes);

    void refreshWxMpRuntimeFromDatabase();

    void saveMenu(WxMpMenuDTO dto);

    void publishMenu(String wxMpConfigCode);

    String fetchRemoteMenu(String wxMpConfigCode);
}

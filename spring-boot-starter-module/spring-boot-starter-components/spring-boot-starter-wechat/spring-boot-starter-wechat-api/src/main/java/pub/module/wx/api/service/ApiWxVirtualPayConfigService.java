package pub.module.wx.api.service;

import pub.module.wx.api.dto.WxVirtualPayConfigDTO;

import java.util.Collection;

/**
 * 微信小程序虚拟支付配置 API（维护库表并刷新运行时）。
 */
public interface ApiWxVirtualPayConfigService {

    void addAndRefreshRuntime(WxVirtualPayConfigDTO dto);

    void updateAndRefreshRuntime(WxVirtualPayConfigDTO dto);

    void removeAndRefreshRuntime(Collection<String> wxVirtualPayConfigCodes);

    void refreshWxVirtualPayRuntimeFromDatabase();
}

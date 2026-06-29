package pub.module.wx.api.service;

import pub.module.wx.api.dto.WxPayConfigDTO;

import java.util.Collection;

/**
 * 微信支付配置 API（维护库表并刷新 WxPay 运行时）。
 */
public interface ApiWxPayConfigService {

    void addAndRefreshRuntime(WxPayConfigDTO dto);

    void updateAndRefreshRuntime(WxPayConfigDTO dto);

    void removeAndRefreshRuntime(Collection<String> wxPayConfigCodes);

    void refreshWxPayRuntimeFromDatabase();
}

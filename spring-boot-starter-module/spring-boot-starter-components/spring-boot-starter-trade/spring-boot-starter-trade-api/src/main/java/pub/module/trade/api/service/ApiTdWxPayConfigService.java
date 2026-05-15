package pub.module.trade.api.service;

import pub.module.trade.api.dto.TdWxPayConfigDTO;

import java.util.Collection;

/**
 * 微信支付配置 API（维护库表并刷新 WxPay 运行时）。
 */
public interface ApiTdWxPayConfigService {

    void addAndRefreshRuntime(TdWxPayConfigDTO dto);

    void updateAndRefreshRuntime(TdWxPayConfigDTO dto);

    void removeAndRefreshRuntime(Collection<String> wxPayConfigCodes);

    void refreshWxPayRuntimeFromDatabase();
}

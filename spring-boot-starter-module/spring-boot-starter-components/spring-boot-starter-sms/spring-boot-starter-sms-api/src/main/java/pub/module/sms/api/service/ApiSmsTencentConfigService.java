package pub.module.sms.api.service;

import pub.module.sms.api.dto.SmsTencentConfigDTO;

import java.util.Collection;

/**
 * 腾讯云短信配置 API（维护库表并刷新运行时）。
 */
public interface ApiSmsTencentConfigService {

    void addAndRefreshRuntime(SmsTencentConfigDTO dto);

    void updateAndRefreshRuntime(SmsTencentConfigDTO dto);

    void removeAndRefreshRuntime(Collection<String> smsTencentConfigCodes);

    void refreshRuntimeFromDatabase();
}

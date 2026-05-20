package pub.module.verification.api.service;

import pub.module.verification.api.dto.NpConfigDTO;

import java.util.Collection;

/**
 * 二要素核验配置（vt_np_config）业务接口：维护后刷新运行时。
 */
public interface ApiNpConfigService {

    void addAndRefreshRuntime(NpConfigDTO dto);

    void updateAndRefreshRuntime(NpConfigDTO dto);

    void removeAndRefreshRuntime(Collection<String> npConfigCodes);

    void refreshRuntimeFromDatabase();
}

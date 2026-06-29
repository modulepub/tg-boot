package pub.module.system.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.system.crud.entity.SysAppConfig;

public interface SysAppConfigService extends IService<SysAppConfig> {

    SysAppConfig getByAppConfigKey(String appConfigKey);

    String normalizeAppConfigValue(String appConfigValue);
}

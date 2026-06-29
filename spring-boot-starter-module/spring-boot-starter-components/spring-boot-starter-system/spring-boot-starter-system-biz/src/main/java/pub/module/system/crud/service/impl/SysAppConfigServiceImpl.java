package pub.module.system.crud.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pub.module.system.crud.entity.SysAppConfig;
import pub.module.system.crud.mapper.SysAppConfigMapper;
import pub.module.system.crud.service.SysAppConfigService;

@Service
public class SysAppConfigServiceImpl extends ServiceImpl<SysAppConfigMapper, SysAppConfig>
        implements SysAppConfigService {

    @Override
    public SysAppConfig getByAppConfigKey(String appConfigKey) {
        if (StrUtil.isBlank(appConfigKey)) {
            return null;
        }
        return getOne(new QueryWrapper<SysAppConfig>().lambda()
                .eq(SysAppConfig::getAppConfigKey, appConfigKey.trim()), false);
    }

    @Override
    public String normalizeAppConfigValue(String appConfigValue) {
        if (StrUtil.isBlank(appConfigValue)) {
            throw new IllegalArgumentException("配置值不能为空");
        }
        String trimmed = appConfigValue.trim();
        if (!JSONUtil.isTypeJSON(trimmed)) {
            throw new IllegalArgumentException("配置值必须是合法 JSON");
        }
        return JSONUtil.parse(trimmed).toString();
    }
}

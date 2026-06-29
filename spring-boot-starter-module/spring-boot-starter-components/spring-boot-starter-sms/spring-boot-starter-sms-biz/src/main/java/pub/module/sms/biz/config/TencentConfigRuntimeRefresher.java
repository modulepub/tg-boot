package pub.module.sms.biz.config;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.sms.crud.entity.SmsTencentConfig;
import pub.module.sms.crud.service.ISmsTencentConfigService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将 sms_tencent_config 中启用的记录加载为腾讯云短信运行时配置。
 */
@Slf4j
@Component
public class TencentConfigRuntimeRefresher {

    private static final String ENABLED_CODE = "1";

    @Resource
    private ISmsTencentConfigService smsTencentConfigService;
    @Resource
    private TencentConfigRuntimeHolder tencentConfigRuntimeHolder;

    public synchronized void refreshFromDatabase() {
        List<SmsTencentConfig> list = smsTencentConfigService.lambdaQuery()
                .eq(SmsTencentConfig::getSmsTencentConfigEnabledCode, ENABLED_CODE)
                .orderByAsc(SmsTencentConfig::getSeqNo)
                .orderByAsc(SmsTencentConfig::getSmsTencentConfigCode)
                .list();
        if (list.isEmpty()) {
            log.warn("sms_tencent_config 无启用配置（sms_tencent_config_enabled_code={}），腾讯云短信将不可用", ENABLED_CODE);
            tencentConfigRuntimeHolder.replace(Map.of(), TencentConfigRuntimeSnapshot.disabled());
            return;
        }
        Map<String, TencentConfigRuntimeSnapshot> map = new LinkedHashMap<>();
        for (SmsTencentConfig row : list) {
            if (StrUtil.hasBlank(row.getSmsTencentConfigCode(), row.getSmsTencentConfigSecretId(),
                    row.getSmsTencentConfigSecretKey(), row.getSmsTencentConfigSdkAppId(),
                    row.getSmsTencentConfigSignName())) {
                log.warn("跳过不完整的腾讯云短信配置 sms_tencent_config_code={}", row.getSmsTencentConfigCode());
                continue;
            }
            TencentConfigRuntimeSnapshot snapshot = TencentConfigRuntimeSnapshot.fromRow(
                    row.getSmsTencentConfigCode(),
                    row.getSmsTencentConfigSecretId(),
                    row.getSmsTencentConfigSecretKey(),
                    row.getSmsTencentConfigSdkAppId(),
                    row.getSmsTencentConfigSignName(),
                    row.getSmsTencentConfigRegion());
            map.put(row.getSmsTencentConfigCode(), snapshot);
        }
        if (map.isEmpty()) {
            log.warn("sms_tencent_config 启用记录均不完整，腾讯云短信将不可用");
            tencentConfigRuntimeHolder.replace(Map.of(), TencentConfigRuntimeSnapshot.disabled());
            return;
        }
        TencentConfigRuntimeSnapshot defaultSnapshot = map.values().iterator().next();
        tencentConfigRuntimeHolder.replace(map, defaultSnapshot);
        log.info("已加载 {} 条腾讯云短信运行时配置，默认 sms_tencent_config_code={}",
                map.size(), defaultSnapshot.getSmsTencentConfigCode());
    }
}

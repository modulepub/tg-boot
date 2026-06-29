package pub.module.verification.biz.config;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.verification.crud.entity.NpConfig;
import pub.module.verification.crud.service.NpConfigService;

import java.util.List;

/**
 * 将 vt_np_config 中启用的记录加载为二要素运行时配置。
 */
@Slf4j
@Component
public class NpConfigRuntimeRefresher {

    private static final String ENABLED_CODE = "1";

    @Resource
    private NpConfigService npConfigService;
    @Resource
    private NpConfigRuntimeHolder npConfigRuntimeHolder;

    public synchronized void refreshFromDatabase() {
        List<NpConfig> list = npConfigService.lambdaQuery()
                .eq(NpConfig::getNpConfigEnabledCode, ENABLED_CODE)
                .orderByAsc(NpConfig::getSeqNo)
                .orderByAsc(NpConfig::getNpConfigCode)
                .list();
        if (list.isEmpty()) {
            log.warn("vt_np_config 无启用配置（np_config_enabled_code={}），二要素渠道将不可用", ENABLED_CODE);
            npConfigRuntimeHolder.replace(NpConfigRuntimeSnapshot.disabled());
            return;
        }
        NpConfig row = list.get(0);
        if (list.size() > 1) {
            log.warn("vt_np_config 存在 {} 条启用配置，仅加载第一条 np_config_code={}", list.size(), row.getNpConfigCode());
        }
        NpConfigRuntimeSnapshot snapshot = NpConfigRuntimeSnapshot.fromRow(
                row.getNpConfigCode(),
                row.getNpConfigProviderCode(),
                row.getNpConfigAccessKeyId(),
                row.getNpConfigAccessKeySecret(),
                row.getNpConfigAuthCode(),
                row.getNpConfigEndpoint(),
                row.getNpConfigMask());
        npConfigRuntimeHolder.replace(snapshot);
        if (snapshot.isAliyunReady()) {
            log.info("已从 vt_np_config 加载二要素配置 np_config_code={} provider={}（Cloudauth Mobile2MetaVerify）",
                    row.getNpConfigCode(), snapshot.getProviderCode());
        }
        else {
            log.warn("vt_np_config 已启用但 AccessKey 不完整 np_config_code={}，二要素调用将返回未配置",
                    row.getNpConfigCode());
        }
    }
}

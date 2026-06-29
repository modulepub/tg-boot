package pub.module.sms.biz.config;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * 腾讯云短信配置运行时持有者（volatile 替换快照，读无锁）。
 */
@Component
public class TencentConfigRuntimeHolder {

    private volatile Map<String, TencentConfigRuntimeSnapshot> configMap = Collections.emptyMap();
    private volatile TencentConfigRuntimeSnapshot defaultSnapshot = TencentConfigRuntimeSnapshot.disabled();

    public TencentConfigRuntimeSnapshot current() {
        return defaultSnapshot;
    }

    public Optional<TencentConfigRuntimeSnapshot> findByCode(String smsTencentConfigCode) {
        if (smsTencentConfigCode == null || smsTencentConfigCode.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(configMap.get(smsTencentConfigCode));
    }

    public void replace(Map<String, TencentConfigRuntimeSnapshot> nextMap,
            TencentConfigRuntimeSnapshot nextDefault) {
        this.configMap = nextMap == null ? Collections.emptyMap() : Map.copyOf(nextMap);
        this.defaultSnapshot = nextDefault == null ? TencentConfigRuntimeSnapshot.disabled() : nextDefault;
    }
}

package pub.module.verification.biz.config;

import org.springframework.stereotype.Component;

/**
 * 二要素配置运行时持有者（volatile 替换快照，读无锁）。
 */
@Component
public class NpConfigRuntimeHolder {

    private volatile NpConfigRuntimeSnapshot snapshot = NpConfigRuntimeSnapshot.disabled();

    public NpConfigRuntimeSnapshot current() {
        return snapshot;
    }

    public void replace(NpConfigRuntimeSnapshot next) {
        this.snapshot = next == null ? NpConfigRuntimeSnapshot.disabled() : next;
    }
}

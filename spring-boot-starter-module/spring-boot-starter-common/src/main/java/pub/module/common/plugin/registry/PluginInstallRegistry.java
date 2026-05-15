package pub.module.common.plugin.registry;

import pub.module.common.plugin.model.PluginInstallRecord;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
/**
 * 启动时写入，运行期只读快照（除全量替换外不设并发修改场景）
 */
public final class PluginInstallRegistry {

    private static final List<PluginInstallRecord> RECORDS = new CopyOnWriteArrayList<>();

    private PluginInstallRegistry() {
    }

    public static void replaceAll(List<PluginInstallRecord> records) {
        RECORDS.clear();
        if (records != null && !records.isEmpty()) {
            RECORDS.addAll(records);
        }
    }

    public static List<PluginInstallRecord> snapshot() {
        return Collections.unmodifiableList(List.copyOf(RECORDS));
    }

    /**
     * 追加记录在内存中尚未出现的 pluginCode（如 Runner 自带的 Maven 依赖插件），避免清空外部目录扫描结果。
     */
    public static void addAllIfAbsentByPluginCode(Collection<PluginInstallRecord> additions) {
        if (additions == null || additions.isEmpty()) {
            return;
        }
        Set<String> existing = RECORDS.stream()
                .map(PluginInstallRecord::getPluginCode)
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.toSet());
        for (PluginInstallRecord r : additions) {
            String code = r.getPluginCode();
            if (code == null || code.isBlank() || "?".equals(code)) {
                continue;
            }
            if (existing.add(code)) {
                RECORDS.add(r);
            }
        }
    }
}

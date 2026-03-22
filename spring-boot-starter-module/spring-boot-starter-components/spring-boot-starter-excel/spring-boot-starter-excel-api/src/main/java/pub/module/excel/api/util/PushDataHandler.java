package pub.module.excel.api.util;

import java.util.Map;

@FunctionalInterface
public interface PushDataHandler {
    String push(Map<String,Object> data);
}

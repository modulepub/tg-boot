package pub.module.ai.biz.service;

import cn.hutool.core.lang.Assert;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AiProviderRegistry {

    private final Map<String, SpiAiChatService> providerMap;

    public AiProviderRegistry(List<SpiAiChatService> services) {
        this.providerMap = services.stream()
                .collect(Collectors.toMap(SpiAiChatService::providerCode, Function.identity(), (a, b) -> a));
    }

    public SpiAiChatService require(String providerCode) {
        SpiAiChatService service = providerMap.get(providerCode);
        Assert.notNull(service, "未注册的 AI 提供商：" + providerCode);
        return service;
    }

    public SpiAiChatService requireOrDefault(String providerCode, String defaultCode) {
        if (providerMap.containsKey(providerCode)) {
            return providerMap.get(providerCode);
        }
        return require(defaultCode);
    }
}

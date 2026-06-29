package pub.module.sms.biz.service;

import cn.hutool.core.lang.Assert;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 短信渠道实现注册表（按 {@link SpiSmsService#providerCode()} 路由，替代 SpringUtil.getBean）。
 */
@Component
public class SmsProviderRegistry {

    private final Map<String, SpiSmsService> providers;

    public SmsProviderRegistry(List<SpiSmsService> providers) {
        this.providers = providers.stream()
                .collect(Collectors.toMap(SpiSmsService::providerCode, Function.identity(), (a, b) -> a));
    }

    public SpiSmsService require(String providerCode) {
        SpiSmsService provider = providers.get(providerCode);
        Assert.notNull(provider, "未找到短信渠道实现：{}", providerCode);
        return provider;
    }
}

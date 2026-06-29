package pub.module.trade.biz.service;

import cn.hutool.core.lang.Assert;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 支付渠道实现注册表（按 {@link BizPayService#paidChannelCode()} 路由，替代 SpringUtil.getBean）。
 */
@Component
public class BizPayServiceRegistry {

    private final Map<String, BizPayService> services;

    public BizPayServiceRegistry(List<BizPayService> services) {
        this.services = services.stream()
                .collect(Collectors.toMap(BizPayService::paidChannelCode, Function.identity(), (a, b) -> a));
    }

    public BizPayService require(String channelCode) {
        BizPayService service = services.get(channelCode);
        Assert.notNull(service, "未知支付渠道：{}", channelCode);
        return service;
    }
}

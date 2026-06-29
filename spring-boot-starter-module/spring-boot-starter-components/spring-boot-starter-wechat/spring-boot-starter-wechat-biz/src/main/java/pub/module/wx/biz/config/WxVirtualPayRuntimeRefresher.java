package pub.module.wx.biz.config;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.wx.crud.entity.WxVirtualPayConfig;
import pub.module.wx.crud.service.WxVirtualPayConfigService;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将库表 wx_virtual_pay_config 中启用的记录加载为内存运行时配置。
 */
@Slf4j
@Component
public class WxVirtualPayRuntimeRefresher {

    private static final String ENABLED_CODE = "1";

    @Resource
    private WxVirtualPayConfigService wxVirtualPayConfigService;

    private volatile Map<String, WxVirtualPayRuntimeConfig> configByAppId = Collections.emptyMap();

    public synchronized void refreshFromDatabase() {
        List<WxVirtualPayConfig> list = wxVirtualPayConfigService.lambdaQuery()
                .eq(WxVirtualPayConfig::getWxVirtualPayConfigEnabledCode, ENABLED_CODE)
                .orderByAsc(WxVirtualPayConfig::getSeqNo)
                .orderByAsc(WxVirtualPayConfig::getWxVirtualPayConfigCode)
                .list();
        if (list.isEmpty()) {
            log.warn("wx_virtual_pay_config 无启用配置，虚拟支付运行时将不可用");
            configByAppId = Collections.emptyMap();
            return;
        }
        Map<String, WxVirtualPayRuntimeConfig> map = new LinkedHashMap<>();
        for (WxVirtualPayConfig row : list) {
            if (StrUtil.hasBlank(row.getWxVirtualPayConfigAppId(), row.getWxVirtualPayConfigOfferId())) {
                log.warn("跳过无效虚拟支付配置 wx_virtual_pay_config_code={}（缺少 appId 或 offerId）",
                        row.getWxVirtualPayConfigCode());
                continue;
            }
            WxVirtualPayRuntimeConfig cfg = new WxVirtualPayRuntimeConfig();
            cfg.setConfigCode(StrUtil.trim(row.getWxVirtualPayConfigCode()));
            cfg.setAppId(StrUtil.trim(row.getWxVirtualPayConfigAppId()));
            cfg.setOfferId(StrUtil.trim(row.getWxVirtualPayConfigOfferId()));
            cfg.setAppKeySandbox(StrUtil.trim(row.getWxVirtualPayConfigAppKeySandbox()));
            cfg.setAppKeyProd(StrUtil.trim(row.getWxVirtualPayConfigAppKeyProd()));
            cfg.setSandbox(row.getWxVirtualPayConfigUseSandbox() != null && row.getWxVirtualPayConfigUseSandbox() == 1);
            cfg.setNotifyUrl(StrUtil.trim(row.getWxVirtualPayConfigNotifyUrl()));
            map.put(cfg.getAppId(), cfg);
        }
        configByAppId = Collections.unmodifiableMap(map);
        log.info("已从 wx_virtual_pay_config 加载 {} 条虚拟支付配置", map.size());
    }

    public WxVirtualPayRuntimeConfig requireByAppId(String appId) {
        Assert.notBlank(appId, "appId 不能为空");
        WxVirtualPayRuntimeConfig cfg = configByAppId.get(appId.trim());
        Assert.notNull(cfg, "未找到 appId 对应的虚拟支付配置：" + appId);
        String appKey = cfg.resolveAppKey();
        Assert.notBlank(appKey, "虚拟支付 AppKey 未配置，请检查 wx_virtual_pay_config");
        return cfg;
    }
}

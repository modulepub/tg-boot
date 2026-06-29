package pub.module.wx.biz.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.wx.crud.entity.WxMiniConfig;
import pub.module.wx.crud.service.WxMiniConfigService;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将库表 wx_mini_config 中启用的记录加载为 WxMaService 多小程序配置。
 */
@Slf4j
@Component
public class WxMaRuntimeRefresher {

    private static final String ENABLED_CODE = "1";

    @Resource
    private WxMaService wxMaService;
    @Resource
    private WxMiniConfigService wxMiniConfigService;

    /**
     * 从数据库刷新 WxMa 运行时配置。
     */
    public synchronized void refreshFromDatabase() {
        List<WxMiniConfig> list = wxMiniConfigService.lambdaQuery()
                .eq(WxMiniConfig::getWxMiniConfigEnabledCode, ENABLED_CODE)
                .orderByAsc(WxMiniConfig::getSeqNo)
                .orderByAsc(WxMiniConfig::getWxMiniConfigCode)
                .list();
        if (list.isEmpty()) {
            log.warn("wx_mini_config 无启用配置（wx_mini_config_enabled_code={}），WxMaService 将清空多小程序配置", ENABLED_CODE);
            wxMaService.setMultiConfigs(Collections.emptyMap());
            return;
        }
        Map<String, WxMaConfig> map = new LinkedHashMap<>();
        for (WxMiniConfig row : list) {
            if (StrUtil.hasBlank(row.getWxMiniConfigAppId(), row.getWxMiniConfigAppSecret())) {
                log.warn("跳过无效微信小程序配置 wx_mini_config_code={}（缺少 appId 或 appSecret）", row.getWxMiniConfigCode());
                continue;
            }
            WxMaDefaultConfigImpl cfg = new WxMaDefaultConfigImpl();
            cfg.setAppid(StrUtil.trim(row.getWxMiniConfigAppId()));
            cfg.setSecret(StrUtil.trim(row.getWxMiniConfigAppSecret()));
            cfg.setMsgDataFormat(StrUtil.blankToDefault(StrUtil.trim(row.getWxMiniConfigMsgDataFormat()), "JSON"));
            cfg.setToken(StrUtil.trim(row.getWxMiniConfigToken()));
            cfg.setAesKey(StrUtil.trim(row.getWxMiniConfigAesKey()));
            map.put(cfg.getAppid(), cfg);
        }
        wxMaService.setMultiConfigs(map);
        log.info("已从 wx_mini_config 加载 {} 条微信小程序配置到 WxMaService", map.size());
    }

    /**
     * 若运行时未加载任何小程序配置，则尝试从数据库刷新一次。
     */
    public void ensureLoaded() {
        if (wxMaService.getWxMaConfig() != null) {
            return;
        }
        refreshFromDatabase();
    }
}

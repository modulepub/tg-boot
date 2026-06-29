package pub.module.wx.biz.config;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.stereotype.Component;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.wx.crud.entity.WxMpConfig;
import pub.module.wx.crud.service.WxMpConfigService;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将库表 wx_mp_config 中启用的记录加载为 WxMpService 多公众号配置。
 */
@Slf4j
@Component
public class WxMpRuntimeRefresher {

    private static final StatusCodeEnum ENABLED = StatusCodeEnum.YES;

    @Resource
    private WxMpService wxMpService;
    @Resource
    private WxMpConfigService wxMpConfigService;

    /**
     * 从数据库刷新 WxMp 运行时配置。
     */
    public synchronized void refreshFromDatabase() {
        List<WxMpConfig> list = wxMpConfigService.lambdaQuery()
                .eq(WxMpConfig::getWxMpConfigEnabledStatusCode, ENABLED)
                .orderByAsc(WxMpConfig::getSeqNo)
                .orderByAsc(WxMpConfig::getWxMpConfigCode)
                .list();
        if (list.isEmpty()) {
            log.warn("wx_mp_config 无启用配置（wx_mp_config_enabled_status_code={}），WxMpService 将清空多公众号配置", ENABLED.getCode());
            wxMpService.setMultiConfigStorages(Collections.emptyMap());
            return;
        }
        Map<String, WxMpConfigStorage> map = new LinkedHashMap<>();
        for (WxMpConfig row : list) {
            if (StrUtil.hasBlank(row.getWxMpConfigAppId(), row.getWxMpConfigAppSecret())) {
                log.warn("跳过无效微信公众号配置 wx_mp_config_code={}（缺少 appId 或 appSecret）", row.getWxMpConfigCode());
                continue;
            }
            WxMpDefaultConfigImpl cfg = new WxMpDefaultConfigImpl();
            cfg.setAppId(StrUtil.trim(row.getWxMpConfigAppId()));
            cfg.setSecret(StrUtil.trim(row.getWxMpConfigAppSecret()));
            cfg.setToken(StrUtil.trim(row.getWxMpConfigToken()));
            cfg.setAesKey(StrUtil.trim(row.getWxMpConfigAesKey()));
            map.put(cfg.getAppId(), cfg);
        }
        wxMpService.setMultiConfigStorages(map);
        log.info("已从 wx_mp_config 加载 {} 条微信公众号配置到 WxMpService", map.size());
    }

    /**
     * 若运行时未加载任何公众号配置，则尝试从数据库刷新一次。
     */
    public void ensureLoaded() {
        if (wxMpService.getWxMpConfigStorage() != null) {
            return;
        }
        refreshFromDatabase();
    }

    /**
     * 确保指定 AppId 的公众号配置已加载到 WxMpService（微信回调入口使用）。
     */
    public void ensureAppLoaded(String appId) {
        if (StrUtil.isBlank(appId)) {
            ensureLoaded();
            return;
        }
        String trimmed = appId.trim();
        wxMpService.switchover(trimmed);
        if (wxMpService.getWxMpConfigStorage() == null
                || !trimmed.equals(wxMpService.getWxMpConfigStorage().getAppId())) {
            refreshFromDatabase();
            wxMpService.switchover(trimmed);
        }
        if (wxMpService.getWxMpConfigStorage() == null) {
            log.warn("未找到 AppId={} 的微信公众号运行时配置，请检查 wx_mp_config 是否已启用且 AppId/Secret 正确", trimmed);
        }
    }
}

package pub.module.wx.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.wx.api.dto.WxMpConfigDTO;
import pub.module.wx.api.dto.WxMpMenuDTO;
import pub.module.wx.api.service.ApiWxMpConfigService;
import pub.module.wx.biz.config.WxMpRuntimeRefresher;
import pub.module.wx.crud.entity.WxMpConfig;
import pub.module.wx.crud.service.WxMpConfigService;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 微信公众号配置业务实现。
 */
@Slf4j
@Service
public class ApiWxMpConfigServiceImpl implements ApiWxMpConfigService {

    @Resource
    private WxMpConfigService wxMpConfigService;
    @Resource
    private WxMpRuntimeRefresher wxMpRuntimeRefresher;
    @Resource
    private WxMpService wxMpService;

    private void normalize(WxMpConfig entity) {
        if (entity.getWxMpConfigEnabledStatusCode() == null) {
            entity.setWxMpConfigEnabledStatusCode(StatusCodeEnum.YES);
        }
        if (entity.getWxMpConfigAiAutoReplyStatusCode() == null) {
            entity.setWxMpConfigAiAutoReplyStatusCode(StatusCodeEnum.NO);
        }
        if (entity.getWxMpConfigSubscribeReplyStatusCode() == null) {
            entity.setWxMpConfigSubscribeReplyStatusCode(StatusCodeEnum.NO);
        }
    }

    private WxMpConfig toEntity(WxMpConfigDTO dto) {
        WxMpConfig entity = BeanUtil.copyProperties(dto, WxMpConfig.class);
        entity.setWxMpConfigEnabledStatusCode(StatusCodeEnum.parse(dto.getWxMpConfigEnabledStatusCode()));
        entity.setWxMpConfigAiAutoReplyStatusCode(StatusCodeEnum.parse(dto.getWxMpConfigAiAutoReplyStatusCode()));
        entity.setWxMpConfigSubscribeReplyStatusCode(StatusCodeEnum.parse(dto.getWxMpConfigSubscribeReplyStatusCode()));
        return entity;
    }

    private void validateForUpsert(WxMpConfig entity) {
        Assert.notBlank(entity.getWxMpConfigCode(), "wx_mp_config_code 不能为空");
        Assert.notBlank(entity.getWxMpConfigAppId(), "wx_mp_config_app_id 不能为空");
        Assert.notBlank(entity.getWxMpConfigAppSecret(), "wx_mp_config_app_secret 不能为空");
        validateSubscribeReply(entity);
    }

    private void validateSubscribeReply(WxMpConfig entity) {
        if (!StatusCodeEnum.isYesValue(entity.getWxMpConfigSubscribeReplyStatusCode())) {
            return;
        }
        Assert.notBlank(entity.getWxMpConfigSubscribeReplyJson(), "开启关注回复时须配置图文内容");
        JSONObject obj = JSONUtil.parseObj(entity.getWxMpConfigSubscribeReplyJson());
        Assert.notBlank(obj.getStr("title"), "图文标题不能为空");
        Assert.notBlank(obj.getStr("picUrl"), "图文封面图不能为空");
        Assert.notBlank(obj.getStr("url"), "图文跳转链接不能为空");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAndRefreshRuntime(WxMpConfigDTO dto) {
        Assert.notNull(dto, "WxMpConfigDTO 不能为空");
        WxMpConfig entity = toEntity(dto);
        validateForUpsert(entity);
        normalize(entity);
        long exists = wxMpConfigService.lambdaQuery()
                .eq(WxMpConfig::getWxMpConfigCode, entity.getWxMpConfigCode())
                .count();
        Assert.isTrue(exists == 0, "微信公众号配置编码已存在");
        wxMpConfigService.save(entity);
        wxMpRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAndRefreshRuntime(WxMpConfigDTO dto) {
        Assert.notNull(dto, "WxMpConfigDTO 不能为空");
        WxMpConfig entity = toEntity(dto);
        validateForUpsert(entity);
        normalize(entity);
        long exists = wxMpConfigService.lambdaQuery()
                .eq(WxMpConfig::getWxMpConfigCode, entity.getWxMpConfigCode())
                .count();
        Assert.isTrue(exists > 0, "微信公众号配置不存在");
        wxMpConfigService.updateById(entity);
        wxMpRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAndRefreshRuntime(Collection<String> wxMpConfigCodes) {
        Assert.notEmpty(wxMpConfigCodes, "请选择要删除的配置");
        wxMpConfigService.removeByBizCodes(wxMpConfigCodes);
        wxMpRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    public void refreshWxMpRuntimeFromDatabase() {
        wxMpRuntimeRefresher.refreshFromDatabase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenu(WxMpMenuDTO dto) {
        Assert.notNull(dto, "WxMpMenuDTO 不能为空");
        Assert.notBlank(dto.getWxMpConfigCode(), "wx_mp_config_code 不能为空");
        validateMenuJson(dto.getMenuJson());
        WxMpConfig config = wxMpConfigService.getByCode(dto.getWxMpConfigCode());
        Assert.notNull(config, "微信公众号配置不存在");
        config.setWxMpConfigMenuJson(dto.getMenuJson());
        wxMpConfigService.updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishMenu(String wxMpConfigCode) {
        Assert.notBlank(wxMpConfigCode, "wx_mp_config_code 不能为空");
        WxMpConfig config = wxMpConfigService.getByCode(wxMpConfigCode);
        Assert.notNull(config, "微信公众号配置不存在");
        Assert.notBlank(config.getWxMpConfigMenuJson(), "请先配置并保存菜单");
        validateMenuJson(config.getWxMpConfigMenuJson());
        wxMpRuntimeRefresher.ensureLoaded();
        wxMpService.switchoverTo(config.getWxMpConfigAppId());
        try {
            wxMpService.getMenuService().menuCreate(config.getWxMpConfigMenuJson());
            config.setWxMpConfigMenuPublishedTime(LocalDateTime.now());
            wxMpConfigService.updateById(config);
            log.info("已发布微信公众号菜单 wx_mp_config_code={} appId={}", wxMpConfigCode, config.getWxMpConfigAppId());
        }
        catch (WxErrorException e) {
            throw new IllegalArgumentException("发布菜单失败：" + mapWxMenuError(e));
        }
    }

    @Override
    public String fetchRemoteMenu(String wxMpConfigCode) {
        Assert.notBlank(wxMpConfigCode, "wx_mp_config_code 不能为空");
        WxMpConfig config = wxMpConfigService.getByCode(wxMpConfigCode);
        Assert.notNull(config, "微信公众号配置不存在");
        wxMpRuntimeRefresher.ensureLoaded();
        wxMpService.switchoverTo(config.getWxMpConfigAppId());
        try {
            WxMpMenu remoteMenu = wxMpService.getMenuService().menuGet();
            if (remoteMenu == null || remoteMenu.getMenu() == null) {
                return "{\"button\":[]}";
            }
            return JSONUtil.toJsonStr(remoteMenu.getMenu());
        }
        catch (WxErrorException e) {
            if (e.getError() != null && e.getError().getErrorCode() == 46003) {
                return "{\"button\":[]}";
            }
            throw new IllegalArgumentException("拉取远程菜单失败：" + e.getError().getErrorMsg());
        }
    }

    private void validateMenuJson(String menuJson) {
        Assert.notBlank(menuJson, "菜单 JSON 不能为空");
        JSONObject obj = JSONUtil.parseObj(menuJson);
        Assert.isTrue(obj.containsKey("button"), "菜单 JSON 须包含 button 数组");
        JSONArray buttons = obj.getJSONArray("button");
        Assert.notNull(buttons, "菜单 button 不能为空");
        validateMenuButtons(buttons, "菜单");
    }

    private void validateMenuButtons(JSONArray buttons, String path) {
        for (int i = 0; i < buttons.size(); i++) {
            JSONObject btn = buttons.getJSONObject(i);
            String name = StrUtil.blankToDefault(btn.getStr("name"), "第" + (i + 1) + "项");
            String current = path + "「" + name + "」";
            JSONArray sub = btn.getJSONArray("sub_button");
            if (sub != null && !sub.isEmpty()) {
                validateMenuButtons(sub, current);
                continue;
            }
            String type = StrUtil.blankToDefault(btn.getStr("type"), "click");
            if ("miniprogram".equals(type)) {
                Assert.notBlank(btn.getStr("appid"), current + "：小程序 AppId 不能为空");
                Assert.notBlank(normalizePagePath(btn.getStr("pagepath")), current + "：小程序页面路径不能为空");
            }
            else if ("view".equals(type)) {
                Assert.notBlank(btn.getStr("url"), current + "：跳转 URL 不能为空");
            }
            else if ("click".equals(type)) {
                Assert.notBlank(btn.getStr("key"), current + "：菜单 Key 不能为空");
            }
        }
    }

    private static String normalizePagePath(String pagepath) {
        if (StrUtil.isBlank(pagepath)) {
            return null;
        }
        String t = StrUtil.trim(pagepath);
        while (t.startsWith("/")) {
            t = t.substring(1);
        }
        return t;
    }

    private static String mapWxMenuError(WxErrorException e) {
        if (e.getError() == null) {
            return e.getMessage();
        }
        if (e.getError().getErrorCode() == 45064) {
            return "公众号无权在菜单中使用该小程序（45064）。"
                    + "请先在微信公众平台「广告与服务 → 小程序管理」关联该小程序，"
                    + "并确认 AppId 正确、页面路径已填写（如 pages/index/index）。";
        }
        return e.getError().getErrorMsg();
    }
}

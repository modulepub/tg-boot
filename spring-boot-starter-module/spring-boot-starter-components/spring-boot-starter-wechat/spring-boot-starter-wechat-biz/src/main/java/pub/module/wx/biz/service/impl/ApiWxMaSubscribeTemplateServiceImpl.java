package pub.module.wx.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.wx.api.dto.WxMaSubscribeTemplateDTO;
import pub.module.wx.api.dto.WxMaSubscribeTemplateFieldDTO;
import pub.module.wx.api.service.ApiWxMaSubscribeTemplateService;
import pub.module.wx.biz.util.WxMaSubscribeTemplateContentParser;
import pub.module.wx.crud.entity.WxMaSubscribeTemplate;
import pub.module.wx.crud.service.WxMaSubscribeTemplateService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApiWxMaSubscribeTemplateServiceImpl implements ApiWxMaSubscribeTemplateService {

    @Resource
    private WxMaSubscribeTemplateService wxMaSubscribeTemplateService;

    private final Map<String, WxMaSubscribeTemplateDTO> cacheByCode = new ConcurrentHashMap<>();
    private final Map<String, WxMaSubscribeTemplateDTO> cacheByTemplateId = new ConcurrentHashMap<>();

    @PostConstruct
    public void warmCache() {
        refreshCache();
    }

    private void refreshCache() {
        cacheByCode.clear();
        cacheByTemplateId.clear();
        for (WxMaSubscribeTemplate row : wxMaSubscribeTemplateService.listAllEnabled()) {
            if (row == null || StrUtil.isBlank(row.getWxMaSubscribeTemplateCode())) {
                continue;
            }
            WxMaSubscribeTemplateDTO dto = toDto(row);
            cacheByCode.put(dto.getWxMaSubscribeTemplateCode(), dto);
            if (StrUtil.isNotBlank(dto.getWxMaSubscribeTemplateId())) {
                cacheByTemplateId.put(dto.getWxMaSubscribeTemplateId().trim(), dto);
            }
        }
    }

    private static WxMaSubscribeTemplateDTO toDto(WxMaSubscribeTemplate row) {
        WxMaSubscribeTemplateDTO dto = new WxMaSubscribeTemplateDTO();
        dto.setWxMaSubscribeTemplateCode(row.getWxMaSubscribeTemplateCode());
        dto.setWxMaSubscribeTemplateId(row.getWxMaSubscribeTemplateId());
        dto.setWxMaSubscribeTemplateContent(row.getWxMaSubscribeTemplateContent());
        return dto;
    }

    @Override
    public WxMaSubscribeTemplateDTO getByCode(String wxMaSubscribeTemplateCode) {
        String code = StrUtil.trimToNull(wxMaSubscribeTemplateCode);
        if (code == null) {
            return null;
        }
        WxMaSubscribeTemplateDTO cached = cacheByCode.get(code);
        if (cached != null) {
            return cached;
        }
        WxMaSubscribeTemplate row = wxMaSubscribeTemplateService.getByCode(code);
        return row == null ? null : toDto(row);
    }

    @Override
    public List<WxMaSubscribeTemplateDTO> listByCodes(Collection<String> wxMaSubscribeTemplateCodes) {
        if (wxMaSubscribeTemplateCodes == null || wxMaSubscribeTemplateCodes.isEmpty()) {
            return List.of();
        }
        List<WxMaSubscribeTemplateDTO> list = new ArrayList<>();
        for (String rawCode : wxMaSubscribeTemplateCodes) {
            WxMaSubscribeTemplateDTO dto = getByCode(rawCode);
            if (dto != null) {
                list.add(dto);
            }
        }
        return list;
    }

    @Override
    public List<WxMaSubscribeTemplateDTO> listAll() {
        if (cacheByCode.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(cacheByCode.values());
    }

    @Override
    public String resolveTemplateId(String wxMaSubscribeTemplateCode) {
        WxMaSubscribeTemplateDTO dto = getByCode(wxMaSubscribeTemplateCode);
        return dto == null ? null : StrUtil.trimToNull(dto.getWxMaSubscribeTemplateId());
    }

    @Override
    public String resolveSceneName(String templateId, String idempotentKeySample) {
        String id = StrUtil.trimToNull(templateId);
        if (id != null) {
            WxMaSubscribeTemplateDTO dto = cacheByTemplateId.get(id);
            if (dto == null) {
                WxMaSubscribeTemplate row = wxMaSubscribeTemplateService.getByTemplateId(id);
                if (row != null) {
                    dto = toDto(row);
                }
            }
            if (dto != null) {
                String sceneName = extractSceneTitle(dto.getWxMaSubscribeTemplateContent());
                if (StrUtil.isNotBlank(sceneName)) {
                    return sceneName;
                }
                return dto.getWxMaSubscribeTemplateCode();
            }
        }
        String key = StrUtil.trimToNull(idempotentKeySample);
        if (key != null) {
            String fromKey = resolveSceneNameFromIdempotentKey(key);
            if (StrUtil.isNotBlank(fromKey)) {
                return fromKey;
            }
        }
        return StrUtil.blankToDefault(id, "—");
    }

    private static String extractSceneTitle(String content) {
        if (StrUtil.isBlank(content)) {
            return null;
        }
        String firstLine = content.trim().split("\\R", 2)[0].trim();
        return StrUtil.isBlank(firstLine) ? null : firstLine;
    }

    private static String resolveSceneNameFromIdempotentKey(String idempotentKey) {
        if (idempotentKey.startsWith("friend_req:")) {
            return "收到好友申请通知";
        }
        if (idempotentKey.startsWith("friend_ok:")) {
            return "添加好友成功通知";
        }
        if (idempotentKey.startsWith("match_req:")) {
            return "牵线请求通知";
        }
        if (idempotentKey.startsWith("free_rec:")) {
            return "相亲对象推荐通知";
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WxMaSubscribeTemplateDTO dto) {
        Assert.notNull(dto, "WxMaSubscribeTemplateDTO 不能为空");
        Assert.notBlank(dto.getWxMaSubscribeTemplateCode(), "wx_ma_subscribe_template_code 不能为空");
        Assert.notBlank(dto.getWxMaSubscribeTemplateId(), "wx_ma_subscribe_template_id 不能为空");
        WxMaSubscribeTemplate existing = wxMaSubscribeTemplateService.getByCode(dto.getWxMaSubscribeTemplateCode().trim());
        Assert.notNull(existing, "订阅消息模板不存在，新增模板请联系开发执行 seed SQL");
        WxMaSubscribeTemplate entity = new WxMaSubscribeTemplate();
        entity.setId(existing.getId());
        entity.setWxMaSubscribeTemplateCode(existing.getWxMaSubscribeTemplateCode());
        entity.setWxMaSubscribeTemplateId(dto.getWxMaSubscribeTemplateId().trim());
        entity.setWxMaSubscribeTemplateContent(StrUtil.trimToNull(dto.getWxMaSubscribeTemplateContent()));
        wxMaSubscribeTemplateService.updateById(entity);
        refreshCache();
    }

    @Override
    public List<WxMaSubscribeTemplateFieldDTO> listFieldsByCode(String wxMaSubscribeTemplateCode) {
        WxMaSubscribeTemplateDTO dto = getByCode(wxMaSubscribeTemplateCode);
        if (dto == null) {
            return List.of();
        }
        return WxMaSubscribeTemplateContentParser.parseFields(dto.getWxMaSubscribeTemplateContent());
    }
}

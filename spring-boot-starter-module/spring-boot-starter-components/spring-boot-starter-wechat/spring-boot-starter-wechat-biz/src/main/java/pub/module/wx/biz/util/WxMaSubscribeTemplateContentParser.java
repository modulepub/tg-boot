package pub.module.wx.biz.util;

import cn.hutool.core.util.StrUtil;
import pub.module.wx.api.dto.WxMaSubscribeTemplateFieldDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 从 wx_ma_subscribe_template_content 解析模板字段（第二行：key 说明 | key 说明）。
 */
public final class WxMaSubscribeTemplateContentParser {

    private WxMaSubscribeTemplateContentParser() {
    }

    public static List<WxMaSubscribeTemplateFieldDTO> parseFields(String content) {
        if (StrUtil.isBlank(content)) {
            return List.of();
        }
        String[] lines = content.trim().split("\\R");
        if (lines.length < 2 || StrUtil.isBlank(lines[1])) {
            return List.of();
        }
        String[] parts = lines[1].trim().split("\\|");
        List<WxMaSubscribeTemplateFieldDTO> list = new ArrayList<>(parts.length);
        for (String part : parts) {
            WxMaSubscribeTemplateFieldDTO field = parsePart(part);
            if (field != null) {
                list.add(field);
            }
        }
        return list;
    }

    private static WxMaSubscribeTemplateFieldDTO parsePart(String part) {
        String trimmed = StrUtil.trimToNull(part);
        if (trimmed == null) {
            return null;
        }
        int spaceIdx = trimmed.indexOf(' ');
        WxMaSubscribeTemplateFieldDTO dto = new WxMaSubscribeTemplateFieldDTO();
        if (spaceIdx > 0) {
            dto.setFieldKey(trimmed.substring(0, spaceIdx).trim());
            dto.setFieldLabel(trimmed.substring(spaceIdx + 1).trim());
        } else {
            dto.setFieldKey(trimmed);
            dto.setFieldLabel(trimmed);
        }
        return StrUtil.isBlank(dto.getFieldKey()) ? null : dto;
    }
}

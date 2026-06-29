package pub.module.wx.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.wx.api.dto.WxMaSubscribeTemplateOptionDTO;
import pub.module.wx.api.service.ApiWxMaSubscribeSendLogService;
import pub.module.wx.api.service.ApiWxMaSubscribeTemplateService;
import pub.module.wx.crud.mapper.WxMaSubscribeSendLogMapper;
import pub.module.wx.crud.model.WxMaSubscribeTemplateGroupRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApiWxMaSubscribeSendLogServiceImpl implements ApiWxMaSubscribeSendLogService {

    @Resource
    private WxMaSubscribeSendLogMapper wxMaSubscribeSendLogMapper;
    @Resource
    private ApiWxMaSubscribeTemplateService apiWxMaSubscribeTemplateService;

    @Override
    public List<WxMaSubscribeTemplateOptionDTO> listTemplateOptions() {
        List<WxMaSubscribeTemplateGroupRow> rows = wxMaSubscribeSendLogMapper.listTemplateGroupStats();
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }
        List<WxMaSubscribeTemplateOptionDTO> list = new ArrayList<>(rows.size());
        for (WxMaSubscribeTemplateGroupRow row : rows) {
            if (row == null || StrUtil.isBlank(row.getTemplateId())) {
                continue;
            }
            String templateId = row.getTemplateId().trim();
            WxMaSubscribeTemplateOptionDTO dto = new WxMaSubscribeTemplateOptionDTO();
            dto.setTemplateId(templateId);
            dto.setSendCount(row.getSendCount() == null ? 0L : row.getSendCount());
            dto.setSceneName(apiWxMaSubscribeTemplateService.resolveSceneName(templateId, row.getSampleIdempotentKey()));
            list.add(dto);
        }
        return list;
    }
}

package pub.module.wx.api.service;

import pub.module.wx.api.dto.WxMaSubscribeTemplateDTO;
import pub.module.wx.api.dto.WxMaSubscribeTemplateFieldDTO;

import java.util.Collection;
import java.util.List;

/**
 * 微信小程序订阅消息模板查询与维护（模板编码由业务侧常量 + seed SQL 维护，后台仅可编辑）。
 */
public interface ApiWxMaSubscribeTemplateService {

    /** 按模板编码查询 */
    WxMaSubscribeTemplateDTO getByCode(String wxMaSubscribeTemplateCode);

    /** 按模板编码批量查询 */
    List<WxMaSubscribeTemplateDTO> listByCodes(Collection<String> wxMaSubscribeTemplateCodes);

    /** 查询全部已配置模板 */
    List<WxMaSubscribeTemplateDTO> listAll();

    /** 按模板编码解析微信 templateId，未配置时返回 null */
    String resolveTemplateId(String wxMaSubscribeTemplateCode);

    /** 按微信 templateId 解析场景展示名（供发送记录筛选项等） */
    String resolveSceneName(String templateId, String idempotentKeySample);

    /** 编辑 templateId / content（不可新增、不可改编码） */
    void update(WxMaSubscribeTemplateDTO dto);

    /** 从模板说明解析字段列表（供管理端测试填参） */
    List<WxMaSubscribeTemplateFieldDTO> listFieldsByCode(String wxMaSubscribeTemplateCode);
}

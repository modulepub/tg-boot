package pub.module.wx.api.service;

import pub.module.wx.api.dto.WxMaSubscribeTemplateOptionDTO;

import java.util.List;

/**
 * 微信小程序订阅消息发送记录（跨 crud 与模板配置的编排能力）。
 */
public interface ApiWxMaSubscribeSendLogService {

    /** 发送记录中已出现的模板 ID（group by），供管理端筛选下拉 */
    List<WxMaSubscribeTemplateOptionDTO> listTemplateOptions();
}

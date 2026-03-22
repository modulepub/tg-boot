package pub.module.wx.biz.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;

import java.util.List;

/**
 * 微信公众号模板消息类
 * 封装微信公众号模板消息内容
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Data
@AllArgsConstructor
public  class MpTmMessage {
    String openId;
    //模板
    String templateId;
    //内容
    List<WxMpTemplateData> data;
}
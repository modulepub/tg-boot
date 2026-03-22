package pub.module.wx.biz.vo;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import lombok.Data;

import java.util.List;

/**
 * 订阅消息类
 * 封装微信小程序订阅消息内容
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Data
public class SendSubMessage {
    //消息体
    List<WxMaSubscribeMessage.MsgData> data;
    //openId
    String openId;
    //模板ID
    String templateId;
}

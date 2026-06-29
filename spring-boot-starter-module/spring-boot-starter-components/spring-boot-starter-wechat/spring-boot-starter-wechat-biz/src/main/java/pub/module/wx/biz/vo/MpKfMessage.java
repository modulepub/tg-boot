package pub.module.wx.biz.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 微信公众号客服消息类
 * 封装微信公众号客服消息内容
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Data
@AllArgsConstructor
public class MpKfMessage {
    //openId
    String openId;
   //内容
    String content;
}

package pub.module.wx.biz.service;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import pub.module.wx.api.dto.WxMpMessageReplyDTO;
import pub.module.wx.crud.entity.WxMpConfig;
import pub.module.wx.crud.entity.WxMpFan;
import pub.module.wx.crud.entity.WxMpMessage;

import java.util.List;

/**
 * 微信公众号消息业务编排。
 */
public interface WxMpMessageBizService {

    /**
     * 处理用户上行消息：入库并视配置触发 AI 自动回复。
     */
    void handleInboundMessage(WxMpConfigContext context, WxMpXmlMessage wxMessage, WxMpService wxMpService);

    /**
     * 管理端人工回复。
     */
    void replyManual(WxMpMessageReplyDTO dto);

    List<WxMpFan> listConversations(String wxMpConfigCode);

    List<WxMpMessage> listMessages(String wxMpConfigCode, String openId);

    /**
     * 更新粉丝昵称（关注事件等场景）。
     */
    void updateFanNickname(String wxMpConfigCode, String openId, String nickname);

    /**
     * 记录关注事件：创建/更新粉丝会话并写入一条系统消息。
     */
    void recordSubscribeEvent(String wxMpConfigCode, String openId, String nickname);

    /**
     * 按配置构建关注回复图文消息；未开启或配置无效时返回 null。
     */
    WxMpXmlOutMessage buildSubscribeReply(WxMpConfig config, WxMpXmlMessage wxMessage);

    record WxMpConfigContext(String wxMpConfigCode, String appId) {
    }
}

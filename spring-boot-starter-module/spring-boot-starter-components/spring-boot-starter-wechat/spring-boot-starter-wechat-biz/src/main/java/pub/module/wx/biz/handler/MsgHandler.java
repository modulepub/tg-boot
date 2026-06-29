package pub.module.wx.biz.handler;

import jakarta.annotation.Resource;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pub.module.wx.biz.service.WxMpMessageBizService;
import pub.module.wx.crud.entity.WxMpConfig;
import pub.module.wx.crud.service.WxMpConfigService;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

/**
 * 消息处理器
 */
@Component
public class MsgHandler extends AbstractHandler {

    @Resource
    private WxMpMessageBizService wxMpMessageBizService;
    @Resource
    private WxMpConfigService wxMpConfigService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        if (!wxMessage.getMsgType().equals(XmlMsgType.EVENT)) {
            WxMpConfig config = resolveConfig(weixinService);
            if (config != null) {
                wxMpMessageBizService.handleInboundMessage(
                        new WxMpMessageBizService.WxMpConfigContext(config.getWxMpConfigCode(), config.getWxMpConfigAppId()),
                        wxMessage,
                        weixinService);
            } else {
                logger.warn("未找到 AppId={} 对应的公众号配置，跳过消息入库",
                        weixinService.getWxMpConfigStorage().getAppId());
            }
        }

        try {
            if (XmlMsgType.TEXT.equals(wxMessage.getMsgType())
                && StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服")
                && weixinService.getKefuService().kfOnlineList()
                .getKfOnlineList().size() > 0) {
                return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE()
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser()).build();
            }
        } catch (WxErrorException e) {
            logger.warn("查询在线客服失败: {}", e.getMessage());
        }

        return null;
    }

    private WxMpConfig resolveConfig(WxMpService weixinService) {
        if (weixinService.getWxMpConfigStorage() == null) {
            return null;
        }
        return wxMpConfigService.findByAppId(weixinService.getWxMpConfigStorage().getAppId());
    }
}

package pub.module.wx.biz.handler;

import java.util.Map;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import org.springframework.stereotype.Component;

/**
 * 文本消息处理器
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Component
public class TextMsgHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> map, WxMpService wxMpService,
                                    WxSessionManager wxSessionManager) throws WxErrorException {
        WxMpConfigStorage wxMpConfigStorage = wxMpService.getWxMpConfigStorage();
        this.logger.info("当前公众号AppId:[{}]；新关注用户 OPENID: [{}]", wxMpConfigStorage.getAppId(), wxMessage.getFromUser());
        //公众号对话处理
        return null;
    }

}


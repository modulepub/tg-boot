package pub.module.wx.biz.handler;

import jakarta.annotation.Resource;
import java.util.Map;

import org.springframework.stereotype.Component;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import pub.module.wx.biz.service.WxMpMessageBizService;
import pub.module.wx.crud.entity.WxMpConfig;
import pub.module.wx.crud.service.WxMpConfigService;

/**
 * 关注事件处理器
 */
@Component
public class SubscribeHandler extends AbstractHandler {

    @Resource
    private WxMpMessageBizService wxMpMessageBizService;
    @Resource
    private WxMpConfigService wxMpConfigService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {
        WxMpConfig config = resolveConfig(weixinService);
        try {
            if (config != null) {
                WxMpUser userWxInfo = weixinService.getUserService()
                        .userInfo(wxMessage.getFromUser(), null);
                logger.info("微信公众号关注 openId={} nickname={}",
                        userWxInfo.getOpenId(), userWxInfo.getNickname());
                wxMpMessageBizService.recordSubscribeEvent(
                        config.getWxMpConfigCode(), userWxInfo.getOpenId(), userWxInfo.getNickname());
            }
        } catch (Exception e) {
            logger.error("获取微信公众号信息错误！", e);
        }

        WxMpXmlOutMessage responseResult = null;
        try {
            responseResult = this.handleSpecial(wxMessage);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        if (responseResult != null) {
            return responseResult;
        }

        if (config != null) {
            try {
                responseResult = wxMpMessageBizService.buildSubscribeReply(config, wxMessage);
                if (responseResult != null) {
                    return responseResult;
                }
            } catch (Exception e) {
                this.logger.error("构建关注回复失败", e);
            }
        }

        return null;
    }

    /**
     * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
     */
    private WxMpXmlOutMessage handleSpecial(WxMpXmlMessage wxMessage) {
        logger.info("扫码来的：{}", wxMessage);
        return null;
    }

    private WxMpConfig resolveConfig(WxMpService weixinService) {
        if (weixinService.getWxMpConfigStorage() == null) {
            return null;
        }
        return wxMpConfigService.findByAppId(weixinService.getWxMpConfigStorage().getAppId());
    }
}

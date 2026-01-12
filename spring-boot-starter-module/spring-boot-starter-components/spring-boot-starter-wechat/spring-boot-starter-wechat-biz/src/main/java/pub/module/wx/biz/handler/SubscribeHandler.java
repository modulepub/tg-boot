package pub.module.wx.biz.handler;

import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import me.chanjar.weixin.mp.builder.outxml.TextBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import pub.module.wx.api.event.WxSubscribeEvent;

import jakarta.annotation.Resource;

/**
 * 关注事件处理器
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Component
public class SubscribeHandler extends AbstractHandler {
    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {

        WxSubscribeEvent.Union union = new WxSubscribeEvent.Union();
        try {
            WxMpUser userWxInfo = weixinService.getUserService()
                    .userInfo(wxMessage.getFromUser(), null);
            BeanUtil.copyProperties(userWxInfo,union);
            // 获取微信用户基本信息
            WxSubscribeEvent wxSubscribeEvent = new WxSubscribeEvent(union);
            eventPublisher.publishEvent(wxSubscribeEvent);

        } catch (Exception e) {
            logger.error("获取微信公众号信息错误！",e);
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

        try {
            return new TextBuilder().build();
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
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

}

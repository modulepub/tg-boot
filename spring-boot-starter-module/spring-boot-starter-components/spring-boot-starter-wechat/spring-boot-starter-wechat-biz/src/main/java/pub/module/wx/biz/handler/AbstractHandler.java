package pub.module.wx.biz.handler;

import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 微信消息处理器抽象基类
 * 提供所有微信消息处理器的公共功能，包括日志记录
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
public abstract class AbstractHandler implements WxMpMessageHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());
}

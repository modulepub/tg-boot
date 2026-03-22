package pub.module.wx.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.context.ApplicationEvent;

/**
 * 微信关注事件类
 * 封装微信公众号用户关注事件信息
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
public class WxSubscribeEvent extends ApplicationEvent {
    Long timestamp;
    @Getter
    Union union;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Union extends WxMpUser{
        String unionId;
        String openId;
    }

    /**
     * 关注事件
     */
    public WxSubscribeEvent(Union union) {
        super(union);
        this.union = union;
        this.timestamp = System.currentTimeMillis();
    }

}

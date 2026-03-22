package pub.module.log.api.event;

import lombok.*;
import org.springframework.context.ApplicationEvent;
import pub.module.log.api.dto.LogDTO;

/**
 * 微信关注事件类
 * 封装微信公众号用户关注事件信息
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LogEvent extends ApplicationEvent {
    LogDTO logDTO;
    /**
     * 关注事件
     */
    public LogEvent(LogDTO logDTO) {
        super(logDTO);
    }

}

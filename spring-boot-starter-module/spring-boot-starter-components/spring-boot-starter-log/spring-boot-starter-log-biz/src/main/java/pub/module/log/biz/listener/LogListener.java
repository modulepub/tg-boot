package pub.module.log.biz.listener;

import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pub.module.log.api.event.LogEvent;
import pub.module.log.curd.entity.BizLog;
import pub.module.log.curd.service.BizLogService;

@Component
public class LogListener {

    @Resource
    BizLogService bizLogService;

    @EventListener
    public void handleUserRegisterEvent(LogEvent logEvent) {
        bizLogService.save(BeanUtil.copyProperties(logEvent.getSource(), BizLog.class));
    }

}
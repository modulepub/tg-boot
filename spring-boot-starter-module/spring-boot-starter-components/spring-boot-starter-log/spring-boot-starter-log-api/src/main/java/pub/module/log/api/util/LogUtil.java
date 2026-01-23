package pub.module.log.api.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import pub.module.log.api.service.BizLogService;

@Slf4j
public class LogUtil {
    public static void record(String logName, String logContent, String logUserCode){
        //通过SpringUtil.getBean 仍然不影响微服务场景，因为微服务场景getBean获取的就是代理类，如feign或者dubbo的代理类
        try {
            SpringUtil.getBean(BizLogService.class).record(logName, logContent, logUserCode);
        }catch (Exception e){
            log.error("理论上本异常不会出现，但是为了确保日志模块的运营一定不会影响业务，所以加了异常捕获：",e);
        }
    }
}

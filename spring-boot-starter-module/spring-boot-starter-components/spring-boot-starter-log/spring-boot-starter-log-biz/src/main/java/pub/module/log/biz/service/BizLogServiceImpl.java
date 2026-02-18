package pub.module.log.biz.service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pub.module.log.api.service.BizLogService;
import pub.module.log.api.util.TransactionUtil;
import pub.module.web.util.IpUtil;
import pub.module.log.curd.entity.Log;
import pub.module.log.curd.service.LogService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class BizLogServiceImpl implements BizLogService {
    @Resource
    LogService logService;
    //如果是微服务独立部署场景，nThreads值可以适当提高
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    public void record(String logName,String methodName, String logContent, String logUserCode) {
        Log log = new Log();
        log.setLogName(logName);
        log.setLogContent(logContent);
        log.setLogTransactionCode(TransactionUtil.getCurrentTransactionCode());
        log.setLogUserCode(logUserCode);
        log.setLogClientIp(this.getIp());
        log.setLogMethodName(methodName);
        executorService.submit(() -> {
            logService.save(log);
        });
    }
    private String getIp(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        HttpServletRequest request = attributes.getRequest();
        return IpUtil.getRealIp(request);
    }
}

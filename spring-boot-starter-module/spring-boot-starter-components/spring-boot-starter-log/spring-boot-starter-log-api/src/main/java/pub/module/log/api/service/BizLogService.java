package pub.module.log.api.service;

/**
 * 日志表 Service
 *
 * @author tg
 * 2026-01-12 01:41:07
 */
public interface BizLogService  {
    void record(String logName,String methodName, String logContent, String logUserCode) ;
}

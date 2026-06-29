package pub.module.common.util.log;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import pub.module.common.util.IpUtil;
import pub.module.common.util.log.dto.LogDTO;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class EasyLog {
    static ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void record(String logName,String logHandleName, Object logContent, String logUserCode) {
        try {
            String logContentStr = "";
            if(logContent instanceof String){
                logContent = logContent.toString();
            }else if (ClassUtil.isBasicType(logContent.getClass())){
                logContent = String.valueOf(logContent);
            }else {
                logContent = JSONUtil.toJsonStr(logContent);
            }
            LogDTO logDTO = LogDTO.builder().build();
            logDTO.setLogClientIp(IpUtil.getRealIp());
            logDTO.setLogTransactionCode(TransactionUtil.getCurrentTransactionCode());
            logDTO.setLogName(logName);
            logDTO.setLogHandleName(logHandleName);
            logDTO.setLogContent(logContentStr);
            logDTO.setLogUserCode(logUserCode);
            logDTO.setLogMethodName(CallerMethodUtil.getCallerMethodName());
            //将结构化数据写入LOG
            for (Field field : LogDTO.class.getDeclaredFields()) {
                Object value = ReflectUtil.getFieldValue(logDTO, field.getName());
                MDC.put(field.getName(), value != null ? value.toString() : "");
            }
            log.info("{}-{}-{}", logName,TransactionUtil.getCurrentTransactionCode(), logContent);
        } catch (Exception e) {
            log.error("理论上本异常不会出现，但是为了确保日志模块的运行一定不会影响业务，所以加了异常捕获：", e);
        }
    }


    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public static String getIp() {
        return IpUtil.getRealIp();
    }

}

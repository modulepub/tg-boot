package pub.module.common.util.log;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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
            logDTO.setLogClientIp(getIp());
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
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        HttpServletRequest request = attributes.getRequest();
        // 1. 优先解析 X-Forwarded-For（多级代理时，第一个IP是客户端真实IP）
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 2. 解析 X-Real-IP（单级代理常用）
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 3. 兜底：获取代理服务器IP（无代理时为客户端IP）
            ip = request.getRemoteAddr();
        }

        // 处理多级代理：X-Forwarded-For可能是 "客户端IP, 代理IP1, 代理IP2"，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // 本地回环地址转换
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip == null ? "unknown" : ip;
    }

}

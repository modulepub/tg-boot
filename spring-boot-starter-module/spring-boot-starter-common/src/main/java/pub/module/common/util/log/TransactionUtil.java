package pub.module.common.util.log;

import cn.hutool.core.util.IdUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * 现事务ID生成
 * 核心特性：无拦截器、无线程包裹、无额外配置、Web新请求新ID、新线程透传TID、不影响登录
 * 所有场景仅调用：TransactionIdUtil.getCurrentTransactionCode()
 * 线程池场景需要在线程方法结束后手动clean一下事务ID，因为线程池场景是基于线程的THREAD_LOCAL实现的，而线程池是复用的线程
 */
public class TransactionUtil {
    // Request中存储TID的唯一KEY（避免与业务参数冲突）
    private static final String TID_REQUEST_KEY = "GLOBAL_TRANSACTION_ID";
    // 临时缓存TID的ThreadLocal（用于Web主线程向新线程透传，新请求会自动覆盖旧值）
    private static final ThreadLocal<String> TID_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 1. SpringBoot Web主线程：新请求=新Request=新TID，同请求复用，自动缓存TID到ThreadLocal供新线程使用
     * 2. 新线程（@Async/自定义线程池/手动Thread）：无请求上下文，直接读取ThreadLocal中主线程的TID，自动透传
     * 3. 非Web场景（定时任务/后台线程）：无请求+无ThreadLocal值，降级生成独立雪花ID
     * 4. Tomcat线程池复用：新请求有全新Request，必生成新TID并覆盖ThreadLocal旧值，彻底解决重复ID问题
     * @return 事务ID（Web+新线程透传同一ID，新请求/非Web场景新ID）
     */
    public static String getCurrentTransactionCode() {
        // 第一步：优先判断Spring Web请求上下文（Web主线程专属，新线程/非Web场景为null）
        RequestAttributes requestAttr = RequestContextHolder.getRequestAttributes();
        if (requestAttr != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttr).getRequest();
            // 从Request获取TID，无则生成新ID并绑定到Request（新请求）
            String tid = (String) request.getAttribute(TID_REQUEST_KEY);
            if (tid == null || tid.trim().isEmpty()) {
                tid = IdUtil.getSnowflakeNextIdStr();
                request.setAttribute(TID_REQUEST_KEY, tid);
            }
            // 同步缓存TID到ThreadLocal，为当前线程内的新线程提供透传依据
            TID_THREAD_LOCAL.set(tid);
            return tid;
        }

        // 第二步：无Web上下文，尝试从ThreadLocal获取主线程缓存的TID（新线程场景）
        String threadLocalTid = TID_THREAD_LOCAL.get();
        if (threadLocalTid != null && !threadLocalTid.trim().isEmpty()) {
            return threadLocalTid;
        }

        threadLocalTid = IdUtil.getSnowflakeNextIdStr();
        TID_THREAD_LOCAL.set(threadLocalTid);
        // 第三步：纯非Web场景（定时任务/无主线程的后台线程），降级生成独立雪花ID
        return threadLocalTid;
    }
}
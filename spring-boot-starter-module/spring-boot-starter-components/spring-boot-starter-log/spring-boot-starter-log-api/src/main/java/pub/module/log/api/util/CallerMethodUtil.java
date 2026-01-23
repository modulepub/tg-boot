package pub.module.log.api.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

/**
 * 方法调用者信息工具类
 * 精准区分工具类内部方法与业务方法，避免误过滤
 */
@Slf4j
public class CallerMethodUtil {

    // 定义工具类的内部方法集合（需过滤的方法）
    private static final Set<String> INTERNAL_METHODS = new HashSet<>(Arrays.asList(
            "getCallerMethodName",
            "getCallerMethodNameByThrowable",
            "getCallerFullInfo",
            "getCallerStackTraceElementByThread",
            "getCallerStackTraceElementByThrowable",
            "getCallerMethodNameByLevel"
    ));

    // ==================== 对外方法（保持不变） ====================
    public static String getCallerMethodName() {
        StackTraceElement callerStackTrace = getCallerStackTraceElementByThread();
        return (callerStackTrace != null) ? callerStackTrace.getMethodName() : "未知调用者";
    }



    public static String getCallerFullInfo() {
        StackTraceElement callerStackTrace = getCallerStackTraceElementByThread();
        if (callerStackTrace != null) {
            return callerStackTrace.getClassName() + "." + callerStackTrace.getMethodName();
        }
        return "未知调用者.未知方法";
    }

    // ==================== 内部核心方法（调整过滤逻辑） ====================
    private static StackTraceElement getCallerStackTraceElementByThread() {
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            if (stackTraceElements.length == 0) {
                return null;
            }

            String currentUtilClassName = CallerMethodUtil.class.getName();
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                String className = stackTraceElement.getClassName();
                String methodName = stackTraceElement.getMethodName();

                // 过滤条件：
                // 1. 是JDK类 → 过滤；
                // 2. 是工具类，但方法是内部工具方法 → 过滤；
                // 3. 其他 → 保留（包括工具类的业务方法，如main）
                if (className.startsWith("java.") || className.startsWith("javax.") || className.startsWith("sun.")) {
                    continue;
                }
                if (className.equals(currentUtilClassName) && INTERNAL_METHODS.contains(methodName)) {
                    continue;
                }
                return stackTraceElement;
            }
        } catch (Exception e) {
            log.error("获取调用者失败（Thread方案）", e);
        }
        return null;
    }


    // ==================== 拓展方法（同步调整过滤逻辑） ====================


    // ==================== 测试方法 ====================
    public static void main(String[] args) {
        String directCaller = getCallerMethodName();
        System.out.println("直接调用者方法名（Thread方案）：" + directCaller); // 输出：main

        String callerFullInfo = getCallerFullInfo();
        System.out.println("直接调用者完整信息：" + callerFullInfo); // 输出：pub.module.log.biz.util.CallerMethodUtil.main

    }
}
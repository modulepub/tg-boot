package pub.module.log.api.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 方法调用者信息工具类
 * 精准区分工具类内部方法与业务方法，避免误过滤
 * 改造后：获取【上上调用方法】的名称/完整信息（调用者的调用者）
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

    // ==================== 对外方法（完全保持不变） ====================
    public static String getCallerMethodName() {
        StackTraceElement callerStackTrace = getCallerStackTraceElementByThread();
        return (callerStackTrace != null) ? callerStackTrace.getMethodName() : "无上上调用者";
    }

    public static String getCallerFullInfo() {
        StackTraceElement callerStackTrace = getCallerStackTraceElementByThread();
        if (callerStackTrace != null) {
            return callerStackTrace.getClassName() + "." + callerStackTrace.getMethodName();
        }
        return "无上上调用者.无方法";
    }

    // ==================== 内部核心方法（核心改造：获取上上调用者栈元素） ====================
    private static StackTraceElement getCallerStackTraceElementByThread() {
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            if (stackTraceElements == null || stackTraceElements.length == 0) {
                return null;
            }

            String currentUtilClassName = CallerMethodUtil.class.getName();
            // 标记：是否已跳过第一个有效调用者（直接调用者）
            boolean skippedFirstCaller = false;

            for (StackTraceElement stackTraceElement : stackTraceElements) {
                String className = stackTraceElement.getClassName();
                String methodName = stackTraceElement.getMethodName();

                // 原有过滤逻辑：跳过JDK类、工具类内部方法
                if (className.startsWith("java.") || className.startsWith("javax.") || className.startsWith("sun.")) {
                    continue;
                }
                if (className.equals(currentUtilClassName) && INTERNAL_METHODS.contains(methodName)) {
                    continue;
                }

                // 核心改造逻辑：
                if (!skippedFirstCaller) {
                    // 第一次找到有效元素：是直接调用者，标记为已跳过，继续遍历
                    skippedFirstCaller = true;
                } else {
                    // 第二次找到有效元素：是上上调用者，直接返回
                    return stackTraceElement;
                }
            }
        } catch (Exception e) {
            log.error("获取上上调用者失败（Thread方案）", e);
        }
        // 遍历结束未找到上上调用者，返回null
        return null;
    }

    // ==================== 测试方法 ====================
    public static void main(String[] args) {
        // 测试1：直接调用（无上上调用者）
        String directCaller = getCallerMethodName();
        System.out.println("上上调用者方法名（直接调用场景）：" + directCaller); // 输出：无上上调用者

        String callerFullInfo = getCallerFullInfo();
        System.out.println("上上调用者完整信息（直接调用场景）：" + callerFullInfo); // 输出：无上上调用者.无方法

        // 测试2：间接调用（有上上调用者）
        testIndirectCall();
    }

    // 间接调用测试方法（作为直接调用者）
    private static void testIndirectCall() {
        String indirectCaller = getCallerMethodName();
        System.out.println("上上调用者方法名（间接调用场景）：" + indirectCaller); // 输出：main
        String indirectFullInfo = getCallerFullInfo();
        System.out.println("上上调用者完整信息（间接调用场景）：" + indirectFullInfo); // 输出：com.tiny.jrh.app.utils.log.CallerMethodUtil.main
    }
}
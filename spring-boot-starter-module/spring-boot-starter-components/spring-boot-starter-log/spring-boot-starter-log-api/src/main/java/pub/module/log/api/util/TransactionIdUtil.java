package pub.module.log.api.util;

import cn.hutool.core.util.IdUtil;

/**
 * 基于Hu tool和ThreadLocal的事务ID工具类（支持并发，线程安全）
 */
public class TransactionIdUtil {

    // 定义ThreadLocal存储当前线程的事务ID，泛型可根据需求选择（UUID/String/雪花算法Long等）
    private static final ThreadLocal<String> THREAD_LOCAL_TRANSACTION_CODE = new ThreadLocal<>();

    /**
     * 获取当前线程的事务ID（不存在则先清理残留值，再生成并存储；存在则直接返回）
     * @return 当前线程对应的唯一事务 ID
     */
    public static String getCurrentTransactionCode() {
        // 1. 先从ThreadLocal中获取已存在的事务ID
        String transactionCode = THREAD_LOCAL_TRANSACTION_CODE.get();

        // 2. 若不存在（说明是新事务/下一个事务），先清理残留值，再生成新事务ID
        if (transactionCode == null || transactionCode.trim().isEmpty()) {
            // 核心优化：下一个事务触发时，自动清理上一个事务的残留值（解决线程复用问题）
            clearTransactionId();

            // 3. 生成新的事务ID（推荐无横线UUID）
            transactionCode = IdUtil.simpleUUID();

            // 4. 将新生成的事务ID存入ThreadLocal，供当前线程同一事务后续调用复用
            THREAD_LOCAL_TRANSACTION_CODE.set(transactionCode);
        }

        // 5. 返回当前线程的事务ID（同一事务内复用，不同事务自动刷新）
        return transactionCode;
    }

    /**
     * 内部清理方法（私有化，避免业务侧误调用；由工具类内部自动触发）
     * 清除当前线程的事务ID，避免线程复用导致的内存泄漏和旧值残留
     */
        private static void clearTransactionId() {
        THREAD_LOCAL_TRANSACTION_CODE.remove();
    }

    // ==================== 多线程并发测试 ====================
    public static void main(String[] args) {
        // 创建5个线程，并发调用工具类方法
        for (int i = 0; i < 5; i++) {
            int threadNum = i + 1;
            new Thread(() -> {
                // 每个线程内多次调用，验证事务ID的复用性
                System.out.printf("线程%d - 事务ID1：%s%n", threadNum, getCurrentTransactionCode());
                System.out.printf("线程%d - 事务ID2：%s%n", threadNum, getCurrentTransactionCode());
                System.out.printf("线程%d - 事务ID3：%s%n", threadNum, getCurrentTransactionCode());
                System.out.println("------------------------");
            }, "测试线程-" + threadNum).start();
        }
    }
}
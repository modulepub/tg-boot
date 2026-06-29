package pub.module.distribution.biz.mock;

import lombok.Data;

/**
 * 业绩测试数据清除结果。
 */
@Data
public class DistPerformanceMockClearResult {

    /** 删除的账单汇总行数（下线） */
    private int summaryCount;
    /** 删除的结算明细行数 */
    private int settleRecordCount;
    /** 删除的幂等事件行数 */
    private int eventCount;
    /** 删除的下线测试用户数 */
    private int userCount;
    /** 重算「下级总付费」的推广人数量 */
    private int promoterRecomputedCount;
    private String message;
}

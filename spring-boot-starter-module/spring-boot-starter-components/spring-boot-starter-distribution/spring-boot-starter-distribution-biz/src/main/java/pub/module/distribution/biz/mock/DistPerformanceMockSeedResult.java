package pub.module.distribution.biz.mock;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 业绩测试数据生成结果。
 */
@Data
public class DistPerformanceMockSeedResult {

    /** 推广人用户编码 */
    private String promoterUserCode;
    /** 推广人展示名（真实姓名/昵称） */
    private String promoterName;
    /** 生成的下线人数 */
    private int downlineCount;
    /** 生成的会员订单总数 */
    private int orderCount;
    /** 下线付费总金额（=推广人「下级客户总付费」增量） */
    private BigDecimal totalPaidAmount = BigDecimal.ZERO;
    /** 下线服务期内总金额（=推广人「下级服务期内总付费」增量） */
    private BigDecimal totalInServiceAmount = BigDecimal.ZERO;
    /** 钻石/黑钻/金钻会员订单数量分布 */
    private int standardMemberOrders;
    private int premiumMemberOrders;
    private int diamondMemberOrders;
    /** 生成的下线测试账号手机号（可用手机号 + 验证码 666666 登录） */
    private List<String> downlinePhones = new ArrayList<>();
    private String message;
}

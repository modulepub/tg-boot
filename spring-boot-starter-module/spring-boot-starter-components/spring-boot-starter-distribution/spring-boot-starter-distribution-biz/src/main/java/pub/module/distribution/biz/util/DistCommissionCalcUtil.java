package pub.module.distribution.biz.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 账单分佣计算：分佣池 = 付费金额 × 商品分佣比例；其中 5% 付费金额奖励上级，剩余归直推邀请人。
 */
public final class DistCommissionCalcUtil {

    private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.9000");
    /** 下级消费奖励上级的比例（占付费金额） */
    private static final BigDecimal SUPERIOR_SHARE_RATE = new BigDecimal("0.0500");

    private DistCommissionCalcUtil() {
    }

    public record CommissionBreakdown(
            BigDecimal commissionPool,
            BigDecimal directCommission,
            BigDecimal superiorCommission) {
    }

    public static CommissionBreakdown calc(BigDecimal paidAmount, BigDecimal goodsCommissionRate) {
        BigDecimal amount = paidAmount == null ? BigDecimal.ZERO : paidAmount;
        BigDecimal rate = goodsCommissionRate != null ? goodsCommissionRate : DEFAULT_COMMISSION_RATE;
        BigDecimal pool = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal superior = amount.multiply(SUPERIOR_SHARE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal direct = pool.subtract(superior);
        if (direct.compareTo(BigDecimal.ZERO) < 0) {
            direct = BigDecimal.ZERO;
        }
        return new CommissionBreakdown(pool, direct, superior);
    }
}

package pub.module.dating.api.constants;

import pub.module.dating.api.service.dto.DtCustomerDTO;

/**
 * 婚恋推荐相关常量。
 */
public final class DatingRecommendConstants {

    /** 未配置会员每日推荐上限时的公益默认额度 */
    public static final int DEFAULT_DAILY_FREE_RECOMMEND_COUNT = 5;

    private DatingRecommendConstants() {
    }

    /** 解析用户每日免费推荐额度（会员权益字段 {@code cusRecommendDayLimit}） */
    public static int resolveDailyRecommendCount(DtCustomerDTO customer) {
        if (customer == null || customer.getCusRecommendDayLimit() == null
                || customer.getCusRecommendDayLimit() <= 0) {
            return DEFAULT_DAILY_FREE_RECOMMEND_COUNT;
        }
        return customer.getCusRecommendDayLimit().intValue();
    }
}

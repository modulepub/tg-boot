package pub.module.distribution.biz.util;

import java.time.LocalDateTime;

/**
 * 根据商品服务期天数计算结束时间。
 */
public final class DistPeriodParseUtil {

    private DistPeriodParseUtil() {
    }

    public static LocalDateTime resolveEndAt(LocalDateTime startAt, Integer tdGdDayPeriod) {
        if (startAt == null || tdGdDayPeriod == null || tdGdDayPeriod <= 0) {
            return startAt;
        }
        return startAt.plusDays(tdGdDayPeriod);
    }

    public static boolean hasServicePeriod(Integer tdGdDayPeriod) {
        return tdGdDayPeriod != null && tdGdDayPeriod > 0;
    }
}

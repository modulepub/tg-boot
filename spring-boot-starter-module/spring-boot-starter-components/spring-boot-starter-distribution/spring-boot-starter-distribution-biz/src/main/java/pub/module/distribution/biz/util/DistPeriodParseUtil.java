package pub.module.distribution.biz.util;

import cn.hutool.core.util.StrUtil;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从商品服务期文案解析结束时间。
 */
public final class DistPeriodParseUtil {

    private static final Pattern DAY_PATTERN = Pattern.compile("(\\d+)\\s*天");

    private DistPeriodParseUtil() {
    }

    public static LocalDateTime resolveEndAt(LocalDateTime startAt, String tdGdPeriod) {
        if (StrUtil.isBlank(tdGdPeriod)) {
            return startAt;
        }
        String period = tdGdPeriod.trim();
        Matcher dayMatcher = DAY_PATTERN.matcher(period);
        if (dayMatcher.find()) {
            long days = Long.parseLong(dayMatcher.group(1));
            return startAt.plusDays(days);
        }
        if (period.contains("半年")) {
            return startAt.plusDays(180);
        }
        if (period.contains("一年") || period.contains("1年")) {
            return startAt.plusDays(365);
        }
        if (period.contains("月")) {
            Matcher monthMatcher = Pattern.compile("(\\d+)\\s*个?月").matcher(period);
            if (monthMatcher.find()) {
                return startAt.plusMonths(Long.parseLong(monthMatcher.group(1)));
            }
            return startAt.plusMonths(1);
        }
        return startAt;
    }

    public static boolean hasServicePeriod(String tdGdPeriod) {
        if (StrUtil.isBlank(tdGdPeriod)) {
            return false;
        }
        LocalDateTime start = LocalDateTime.now();
        return resolveEndAt(start, tdGdPeriod).isAfter(start);
    }
}

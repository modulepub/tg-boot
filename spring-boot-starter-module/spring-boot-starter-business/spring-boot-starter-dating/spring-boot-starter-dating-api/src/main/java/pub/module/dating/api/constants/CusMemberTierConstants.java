package pub.module.dating.api.constants;

/**
 * 会员套餐（vip 类目）商品编码与权益配额。
 */
public final class CusMemberTierConstants {

    /** 商品类目：会员套餐 */
    public static final String CGY_CODE_VIP = "vip";

    /** standardMember → 钻石会员 */
    public static final String STANDARD_MEMBER = "standardMember";
    /** premiumMember → 黑钻会员 */
    public static final String PREMIUM_MEMBER = "premiumMember";
    /** diamondMember → 金钻会员 */
    public static final String DIAMOND_MEMBER = "diamondMember";
    /** freevip → 赠送会员（后台代客赠送，跳过付费流程） */
    public static final String FREE_VIP = "freevip";

    public static final String STANDARD_MEMBER_NAME = "钻石会员";
    public static final String PREMIUM_MEMBER_NAME = "黑钻会员";
    public static final String DIAMOND_MEMBER_NAME = "金钻会员";
    public static final String FREE_VIP_NAME = "赠送会员";

    /** 赠送会员默认服务期（天）：体验会员 7 天 */
    public static final int FREE_VIP_SERVICE_DAYS = 7;
    /** 赠送会员每日权益配额（加好友 / 推荐 / 牵线） */
    public static final int FREE_VIP_DAILY_QUOTA = 20;

    /** 无服务期时的默认天数 */
    public static final int DEFAULT_SERVICE_DAYS = 365;

    private CusMemberTierConstants() {
    }

    public static boolean isVipCategory(String tdGdCgyCode) {
        return CGY_CODE_VIP.equals(tdGdCgyCode);
    }

    /**
     * 三类会员每日权益配额（加好友 / 推荐 / 牵线相同）。
     */
    public static long resolveMemberDailyQuota(String memberTypeCode) {
        if (memberTypeCode == null) {
            return 20L;
        }
        return switch (memberTypeCode.trim()) {
            case PREMIUM_MEMBER -> 40L;
            case DIAMOND_MEMBER -> 60L;
            case STANDARD_MEMBER -> 20L;
            case FREE_VIP -> (long) FREE_VIP_DAILY_QUOTA;
            default -> 20L;
        };
    }

    public static int resolveServiceDays(Integer tdGdDayPeriod) {
        if (tdGdDayPeriod == null || tdGdDayPeriod <= 0) {
            return DEFAULT_SERVICE_DAYS;
        }
        return tdGdDayPeriod;
    }

    /**
     * 按商品编码解析会员类型名称（与后台商品名称一致）。
     */
    public static String resolveMemberTypeName(String memberTypeCode) {
        if (memberTypeCode == null) {
            return STANDARD_MEMBER_NAME;
        }
        return switch (memberTypeCode.trim()) {
            case PREMIUM_MEMBER -> PREMIUM_MEMBER_NAME;
            case DIAMOND_MEMBER -> DIAMOND_MEMBER_NAME;
            case STANDARD_MEMBER -> STANDARD_MEMBER_NAME;
            case FREE_VIP -> FREE_VIP_NAME;
            default -> memberTypeCode.trim();
        };
    }
}

package pub.module.distribution.biz.mock;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.distribution.api.constants.DistBizLineCodeEnum;
import pub.module.distribution.biz.service.internal.DistUserBillSummaryMaintainer;
import pub.module.distribution.crud.entity.DistUserBillSettleRecord;
import pub.module.distribution.crud.entity.DistUserBillSummary;
import pub.module.distribution.crud.mapper.DistUserBillSettleRecordMapper;
import pub.module.distribution.crud.mapper.DistUserBillSummaryMapper;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.trade.api.dto.TdOrderGoodsDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 业绩（绩效）测试数据生成：为选定推广人生成若干下线测试账号，
 * 下线下不同类型的会员订单，复用真实账单维护逻辑（{@link DistUserBillSummaryMaintainer#onOrderGoodsPaid}）
 * 累加到推广人的「下级客户总付费 / 下级服务期内总付费」，前端推广明细随之展示。
 */
@Slf4j
@Service
public class DistPerformanceMockSeedService {

    private static final StatusCodeEnum TEST = StatusCodeEnum.YES;
    private static final String BIZ_LINE = DistBizLineCodeEnum.DATING.getCode();
    private static final String MEMBER_CGY_CODE = "vip";
    /** 会员套餐沿用平台默认分佣比例 */
    private static final BigDecimal MEMBER_COMMISSION_RATE = new BigDecimal("0.0500");

    private static final int MAX_DOWNLINE = 500;
    /** 每个下线随机开 1~2 单会员 */
    private static final int MAX_ORDERS_PER_DOWNLINE = 2;
    /** 会员服务期：一个月（30 天）。下线均在近 20 天内注册，故均未出服务期，金额都计入服务期内 */
    private static final int MEMBER_SERVICE_DAYS = 30;

    private static final String SURNAMES = "赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹";
    private static final String GIVEN = "伟芳娜秀英敏静丽强磊洋勇艳杰娟涛明超霞平刚桂兰梅琳鹏宇婷";

    /** 会员套餐类型：商品编码、名称、价格 */
    private record MemberTier(String code, String name, BigDecimal price) {
    }

    // 客单价排除 6998（金钻会员），仅保留 368 / 698 两档会员
    private static final List<MemberTier> TIERS = List.of(
            new MemberTier("standardMember", "钻石会员", new BigDecimal("368")),
            new MemberTier("premiumMember", "黑钻会员", new BigDecimal("698"))
    );

    @Resource
    private ApiSysUserService apiSysUserService;
    @Resource
    private DistUserBillSummaryMaintainer distUserBillSummaryMaintainer;
    @Resource
    private DistUserBillSummaryMapper distUserBillSummaryMapper;
    @Resource
    private DistUserBillSettleRecordMapper distUserBillSettleRecordMapper;

    @Transactional(rollbackFor = Exception.class)
    public DistPerformanceMockSeedResult seed(String promoterUserCode, Integer downlineCount) {
        String promoterCode = StrUtil.trimToNull(promoterUserCode);
        Assert.notBlank(promoterCode, "请选择要生成业绩数据的推广人");
        UserDTO promoter = apiSysUserService.getUserByUserCode(promoterCode);
        Assert.notNull(promoter, "推广人不存在：" + promoterCode);

        int downlines = clamp(downlineCount, 1, MAX_DOWNLINE);

        // 确保推广人存在账单汇总行，作为「下级总付费」累加载体
        distUserBillSummaryMaintainer.initOnUserRegistered(promoter, BIZ_LINE);

        DistPerformanceMockSeedResult result = new DistPerformanceMockSeedResult();
        result.setPromoterUserCode(promoterCode);
        result.setPromoterName(resolveName(promoter));

        Random r = new Random();
        for (int i = 0; i < downlines; i++) {
            String phone = randomPhone(r);
            String realName = randomName(r);
            UserDTO downline = ensureTestDownline(phone, realName);
            // 建立邀请关系：下线的推荐人 = 推广人
            apiSysUserService.setReferenceUserCodeIfAbsent(downline.getUserCode(), promoterCode);

            // 固定每个下线随机开 1~2 单会员
            int orders = 1 + r.nextInt(MAX_ORDERS_PER_DOWNLINE);
            for (int j = 0; j < orders; j++) {
                MemberTier tier = TIERS.get(r.nextInt(TIERS.size()));
                TdOrderGoodsDTO dto = buildMemberOrder(downline, realName, phone, tier);
                distUserBillSummaryMaintainer.onOrderGoodsPaid(dto);

                result.setOrderCount(result.getOrderCount() + 1);
                result.setTotalPaidAmount(result.getTotalPaidAmount().add(tier.price()));
                // 注册在近 20 天内、服务期 30 天，均未出服务期，金额都计入服务期内
                result.setTotalInServiceAmount(result.getTotalInServiceAmount().add(tier.price()));
                countTier(result, tier);
            }
            // 把下线注册（账单汇总）及结算明细时间随机回拨到最近 20 天内，避免全部显示为今天
            backdateDownlineTime(downline.getUserCode(), randomPastTime(r));
            result.getDownlinePhones().add(phone);
        }
        result.setDownlineCount(downlines);
        result.setMessage(String.format(
                "已为「%s」生成 %d 位下线、%d 笔会员订单，下级客户总付费 +¥%s（钻石 %d / 黑钻 %d / 金钻 %d）",
                result.getPromoterName(), result.getDownlineCount(), result.getOrderCount(),
                result.getTotalPaidAmount().stripTrailingZeros().toPlainString(),
                result.getStandardMemberOrders(), result.getPremiumMemberOrders(), result.getDiamondMemberOrders()));
        log.info("dist performance mock seed done: {}", result);
        return result;
    }

    private UserDTO ensureTestDownline(String phone, String realName) {
        UserDTO user = apiSysUserService.registerByPhone(phone, null);
        UserDTO patch = new UserDTO();
        patch.setUserCode(user.getUserCode());
        patch.setUserPhone(phone);
        patch.setUserRealName(realName);
        patch.setUserTestStatusCode(TEST);
        apiSysUserService.updateById(patch);
        return apiSysUserService.getUserByUserCode(user.getUserCode());
    }

    private TdOrderGoodsDTO buildMemberOrder(UserDTO downline, String realName, String phone, MemberTier tier) {
        TdOrderGoodsDTO dto = new TdOrderGoodsDTO();
        dto.setTdOdSysUserCode(downline.getUserCode());
        dto.setTdOdSysUserRealName(realName);
        dto.setTdOdSysUserPhone(phone);
        dto.setTdOdCode(IdUtil.getSnowflakeNextIdStr());
        dto.setTdOdGdCode(IdUtil.getSnowflakeNextIdStr());
        dto.setTdGdCode(tier.code());
        dto.setTdGdName(tier.name());
        dto.setTdGdPrice(tier.price());
        dto.setTdGdValue(tier.price());
        dto.setTdOdGdAmount(tier.price());
        dto.setTdOdGdNum(BigDecimal.ONE);
        // 会员服务期一个月，计入服务期内（下线均在近 20 天内注册，未出服务期）
        dto.setTdGdDayPeriod(MEMBER_SERVICE_DAYS);
        dto.setTdGdCommissionRate(MEMBER_COMMISSION_RATE);
        dto.setTdGdCgyCode(MEMBER_CGY_CODE);
        dto.setTdOdPaidCode(StatusCodeEnum.YES.getCode());
        dto.setCreateTime(LocalDateTime.now());
        return dto;
    }

    private void backdateDownlineTime(String downlineUserCode, LocalDateTime registeredAt) {
        distUserBillSummaryMapper.update(null, new UpdateWrapper<DistUserBillSummary>().lambda()
                .eq(DistUserBillSummary::getDistUserCode, downlineUserCode)
                .eq(DistUserBillSummary::getDistBizLineCode, BIZ_LINE)
                .set(DistUserBillSummary::getCreateTime, registeredAt));
        // 服务期结束时间按注册时间推算（注册 + 30 天），保证未出服务期、金额仍在服务期内
        distUserBillSettleRecordMapper.update(null, new UpdateWrapper<DistUserBillSettleRecord>().lambda()
                .eq(DistUserBillSettleRecord::getDistPayerUserCode, downlineUserCode)
                .eq(DistUserBillSettleRecord::getDistBizLineCode, BIZ_LINE)
                .set(DistUserBillSettleRecord::getCreateTime, registeredAt)
                .set(DistUserBillSettleRecord::getDistServicePeriodEndAt, registeredAt.plusDays(MEMBER_SERVICE_DAYS)));
    }

    /** 最近 20 天内的随机时间 */
    private static LocalDateTime randomPastTime(Random r) {
        long maxMinutes = 20L * 24 * 60;
        long offsetMinutes = (long) (r.nextDouble() * maxMinutes);
        return LocalDateTime.now().minusMinutes(offsetMinutes);
    }

    private static void countTier(DistPerformanceMockSeedResult result, MemberTier tier) {
        switch (tier.code()) {
            case "standardMember" -> result.setStandardMemberOrders(result.getStandardMemberOrders() + 1);
            case "premiumMember" -> result.setPremiumMemberOrders(result.getPremiumMemberOrders() + 1);
            case "diamondMember" -> result.setDiamondMemberOrders(result.getDiamondMemberOrders() + 1);
            default -> {
            }
        }
    }

    private static String resolveName(UserDTO user) {
        if (user == null) {
            return "";
        }
        if (StrUtil.isNotBlank(user.getUserRealName())) {
            return user.getUserRealName().trim();
        }
        if (StrUtil.isNotBlank(user.getUserNickName())) {
            return user.getUserNickName().trim();
        }
        return user.getUserCode();
    }

    private static String randomName(Random r) {
        StringBuilder sb = new StringBuilder();
        sb.append(SURNAMES.charAt(r.nextInt(SURNAMES.length())));
        int len = 1 + r.nextInt(2);
        for (int k = 0; k < len; k++) {
            sb.append(GIVEN.charAt(r.nextInt(GIVEN.length())));
        }
        return sb.toString();
    }

    private static String randomPhone(Random r) {
        StringBuilder sb = new StringBuilder("1");
        sb.append(3 + r.nextInt(7));
        for (int k = 0; k < 9; k++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    private static int clamp(Integer value, int min, int max) {
        if (value == null) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }
}

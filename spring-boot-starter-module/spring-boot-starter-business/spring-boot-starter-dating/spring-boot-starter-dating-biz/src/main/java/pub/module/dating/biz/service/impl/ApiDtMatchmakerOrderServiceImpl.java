package pub.module.dating.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.ApiDtMatchmakerOrderService;
import pub.module.dating.api.service.dto.MkServiceOrderItemDTO;
import pub.module.dating.api.service.dto.MkServiceOrderStatsDTO;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.service.DtMatchmakerService;
import pub.module.trade.api.dto.TdOrderGoodsDTO;
import pub.module.trade.api.service.ApiTdOrderGoodsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 红娘工作台-服务订单
 */
@Service
public class ApiDtMatchmakerOrderServiceImpl implements ApiDtMatchmakerOrderService {

    private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.9000");
    private static final BigDecimal SUPERIOR_SHARE_RATE = new BigDecimal("0.0500");
    private static final String PAID_STATUS_CODE = "1";

    @Resource
    private ApiTdOrderGoodsService apiTdOrderGoodsService;
    @Resource
    private ApiDtCustomerService apiDtCustomerService;
    @Resource
    private DtMatchmakerService dtMatchmakerService;

    @Override
    public IPage<MkServiceOrderItemDTO> listServiceOrders(String matchmakerUserCode, Integer pageNo, Integer pageSize) {
        String userCode = StrUtil.trim(matchmakerUserCode);
        if (StrUtil.isBlank(userCode)) {
            return emptyPage(pageNo, pageSize);
        }
        DtMatchmaker matchmaker = dtMatchmakerService.getOne(new QueryWrapper<DtMatchmaker>().lambda()
            .eq(DtMatchmaker::getMkUserCode, userCode), false);
        if (matchmaker == null || StrUtil.isBlank(matchmaker.getMkUserCode())) {
            return emptyPage(pageNo, pageSize);
        }

        IPage<TdOrderGoodsDTO> orderPage = apiTdOrderGoodsService.pageByTdGdSysUserCode(userCode, pageNo, pageSize);
        List<String> ordererUserCodes = orderPage.getRecords().stream()
            .map(TdOrderGoodsDTO::getTdOdSysUserCode)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, DtCustomerDTO> customerByUserCode = ordererUserCodes.isEmpty()
            ? Collections.emptyMap()
            : apiDtCustomerService.listByUserCodes(ordererUserCodes).stream()
                .filter(c -> StrUtil.isNotBlank(c.getCusUserCode()))
                .collect(Collectors.toMap(c -> c.getCusUserCode().trim(), c -> c, (a, b) -> a));

        return orderPage.convert(row -> toItem(row, customerByUserCode.get(StrUtil.trim(row.getTdOdSysUserCode()))));
    }

    @Override
    public MkServiceOrderStatsDTO getServiceOrderStats(String matchmakerUserCode) {
        MkServiceOrderStatsDTO stats = new MkServiceOrderStatsDTO();
        stats.setTotalOrderAmount(BigDecimal.ZERO);
        stats.setTotalCommissionAmount(BigDecimal.ZERO);
        stats.setUnlockedCommissionAmount(BigDecimal.ZERO);

        String userCode = StrUtil.trim(matchmakerUserCode);
        if (StrUtil.isBlank(userCode)) {
            return stats;
        }
        DtMatchmaker matchmaker = dtMatchmakerService.getOne(new QueryWrapper<DtMatchmaker>().lambda()
            .eq(DtMatchmaker::getMkUserCode, userCode), false);
        if (matchmaker == null || StrUtil.isBlank(matchmaker.getMkUserCode())) {
            return stats;
        }

        LocalDateTime now = LocalDateTime.now();
        BigDecimal totalOrderAmount = BigDecimal.ZERO;
        BigDecimal totalCommissionAmount = BigDecimal.ZERO;
        BigDecimal unlockedCommissionAmount = BigDecimal.ZERO;

        for (TdOrderGoodsDTO row : apiTdOrderGoodsService.listByTdGdSysUserCode(userCode)) {
            if (!isPaid(row.getTdOdPaidCode())) {
                continue;
            }
            BigDecimal amount = row.getTdOdGdAmount() != null ? row.getTdOdGdAmount() : BigDecimal.ZERO;
            BigDecimal commission = calcExpectedCommission(amount, resolveCommissionRate(row.getTdGdCommissionRate()));
            totalOrderAmount = totalOrderAmount.add(amount);
            totalCommissionAmount = totalCommissionAmount.add(commission);
            if (isCommissionUnlocked(row.getCreateTime(), row.getTdGdDayPeriod(), now)) {
                unlockedCommissionAmount = unlockedCommissionAmount.add(commission);
            }
        }

        stats.setTotalOrderAmount(totalOrderAmount.setScale(2, RoundingMode.HALF_UP));
        stats.setTotalCommissionAmount(totalCommissionAmount.setScale(2, RoundingMode.HALF_UP));
        stats.setUnlockedCommissionAmount(unlockedCommissionAmount.setScale(2, RoundingMode.HALF_UP));
        return stats;
    }

    private static boolean isPaid(String paidCode) {
        return PAID_STATUS_CODE.equals(StrUtil.trim(paidCode));
    }

    /**
     * 无服务期立即解锁；有服务期则以下单时间 + 服务天数为结束时间。
     */
    private static boolean isCommissionUnlocked(LocalDateTime createTime, Integer tdGdDayPeriod, LocalDateTime now) {
        if (tdGdDayPeriod == null || tdGdDayPeriod <= 0) {
            return true;
        }
        if (createTime == null) {
            return false;
        }
        return !createTime.plusDays(tdGdDayPeriod).isAfter(now);
    }

    private static MkServiceOrderItemDTO toItem(TdOrderGoodsDTO row, DtCustomerDTO customer) {
        MkServiceOrderItemDTO item = new MkServiceOrderItemDTO();
        item.setTdOdGdCode(row.getTdOdGdCode());
        item.setTdGdName(row.getTdGdName());
        item.setTdOdGdAmount(row.getTdOdGdAmount());
        item.setTdGdDayPeriod(row.getTdGdDayPeriod());
        item.setCreateTime(row.getCreateTime());
        item.setTdOdSysUserCode(row.getTdOdSysUserCode());
        item.setTdOdPaidCode(row.getTdOdPaidCode());
        item.setTdGdCommissionRate(resolveCommissionRate(row.getTdGdCommissionRate()));
        item.setExpectedCommissionAmount(calcExpectedCommission(row.getTdOdGdAmount(), item.getTdGdCommissionRate()));
        if (customer != null) {
            item.setCusCode(customer.getCusCode());
            item.setCusPhone(StrUtil.blankToDefault(customer.getCusPhone(), row.getTdOdSysUserPhone()));
            item.setCusName(resolveDisplayName(customer, row.getTdOdSysUserRealName()));
        } else {
            item.setCusPhone(row.getTdOdSysUserPhone());
            item.setCusName(StrUtil.blankToDefault(row.getTdOdSysUserRealName(), "客户"));
        }
        return item;
    }

    private static String resolveDisplayName(DtCustomerDTO customer, String fallback) {
        String nick = StrUtil.trim(customer.getCusNickName());
        if (StrUtil.isNotBlank(nick)) {
            return nick;
        }
        String name = StrUtil.trim(customer.getCusName());
        if (StrUtil.isNotBlank(name)) {
            return name;
        }
        return StrUtil.blankToDefault(fallback, "客户");
    }

    private static BigDecimal resolveCommissionRate(BigDecimal rate) {
        return rate != null ? rate : DEFAULT_COMMISSION_RATE;
    }

    /**
     * 预计佣金 = 分佣池 - 上级分佣，与 {@code DistCommissionCalcUtil} 直推分佣口径一致。
     */
    private static BigDecimal calcExpectedCommission(BigDecimal paidAmount, BigDecimal goodsCommissionRate) {
        BigDecimal amount = paidAmount != null ? paidAmount : BigDecimal.ZERO;
        BigDecimal rate = goodsCommissionRate != null ? goodsCommissionRate : DEFAULT_COMMISSION_RATE;
        BigDecimal pool = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal superior = amount.multiply(SUPERIOR_SHARE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal direct = pool.subtract(superior);
        return direct.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : direct;
    }

    private static IPage<MkServiceOrderItemDTO> emptyPage(Integer pageNo, Integer pageSize) {
        int pn = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int ps = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pn, ps, 0);
    }
}

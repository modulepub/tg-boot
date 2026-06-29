package pub.module.distribution.biz.mock;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.distribution.api.constants.DistBizLineCodeEnum;
import pub.module.distribution.crud.entity.DistUserBillEvent;
import pub.module.distribution.crud.entity.DistUserBillSettleRecord;
import pub.module.distribution.crud.entity.DistUserBillSummary;
import pub.module.distribution.crud.mapper.DistUserBillEventMapper;
import pub.module.distribution.crud.mapper.DistUserBillSettleRecordMapper;
import pub.module.distribution.crud.mapper.DistUserBillSummaryMapper;
import pub.module.system.api.service.ApiSysUserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 清除业绩测试数据：删除标记为测试的下线账单汇总 / 结算明细 / 幂等事件 + 下线测试账号，
 * 并按剩余真实下线重算推广人的「下级总付费」，使被选推广人恢复到生成前的真实业绩。
 */
@Slf4j
@Service
public class DistPerformanceMockClearService {

    private static final String BIZ_LINE = DistBizLineCodeEnum.DATING.getCode();
    private static final String TEST = StatusCodeEnum.YES.getCode();

    @Resource
    private DistUserBillSummaryMapper distUserBillSummaryMapper;
    @Resource
    private DistUserBillSettleRecordMapper distUserBillSettleRecordMapper;
    @Resource
    private DistUserBillEventMapper distUserBillEventMapper;
    @Resource
    private ApiSysUserService apiSysUserService;

    @Transactional(rollbackFor = Exception.class)
    public DistPerformanceMockClearResult clear() {
        DistPerformanceMockClearResult result = new DistPerformanceMockClearResult();

        List<DistUserBillSummary> testSummaries = distUserBillSummaryMapper.selectList(
                new QueryWrapper<DistUserBillSummary>().lambda()
                        .eq(DistUserBillSummary::getDistTestStatusCode, TEST));

        Set<String> downlineCodes = new LinkedHashSet<>();
        Set<String> affectedPromoters = new LinkedHashSet<>();
        for (DistUserBillSummary summary : testSummaries) {
            if (StrUtil.isNotBlank(summary.getDistUserCode())) {
                downlineCodes.add(summary.getDistUserCode().trim());
            }
            if (StrUtil.isNotBlank(summary.getDistInviterUserCode())) {
                affectedPromoters.add(summary.getDistInviterUserCode().trim());
            }
        }
        affectedPromoters.removeAll(downlineCodes);

        result.setSettleRecordCount(distUserBillSettleRecordMapper.delete(
                new QueryWrapper<DistUserBillSettleRecord>().lambda()
                        .eq(DistUserBillSettleRecord::getDistTestStatusCode, TEST)));
        result.setEventCount(distUserBillEventMapper.delete(
                new QueryWrapper<DistUserBillEvent>().lambda()
                        .eq(DistUserBillEvent::getDistTestStatusCode, TEST)));
        result.setSummaryCount(distUserBillSummaryMapper.delete(
                new QueryWrapper<DistUserBillSummary>().lambda()
                        .eq(DistUserBillSummary::getDistTestStatusCode, TEST)));

        int userCount = 0;
        for (String downlineCode : downlineCodes) {
            apiSysUserService.deleteByCode(downlineCode);
            userCount++;
        }
        result.setUserCount(userCount);

        int recomputed = 0;
        for (String promoterCode : affectedPromoters) {
            if (recomputePromoterSub(promoterCode)) {
                recomputed++;
            }
        }
        result.setPromoterRecomputedCount(recomputed);

        result.setMessage(String.format(
                "测试业绩数据已清除：汇总 %d、结算明细 %d、事件 %d、下线用户 %d，重算推广人 %d",
                result.getSummaryCount(), result.getSettleRecordCount(), result.getEventCount(),
                result.getUserCount(), result.getPromoterRecomputedCount()));
        log.info("dist performance mock clear done: {}", result);
        return result;
    }

    private boolean recomputePromoterSub(String promoterCode) {
        DistUserBillSummary promoterRow = distUserBillSummaryMapper.selectOne(
                new QueryWrapper<DistUserBillSummary>().lambda()
                        .eq(DistUserBillSummary::getDistUserCode, promoterCode)
                        .eq(DistUserBillSummary::getDistBizLineCode, BIZ_LINE), false);
        if (promoterRow == null) {
            return false;
        }
        List<DistUserBillSummary> remaining = distUserBillSummaryMapper.selectList(
                new QueryWrapper<DistUserBillSummary>().lambda()
                        .eq(DistUserBillSummary::getDistInviterUserCode, promoterCode)
                        .eq(DistUserBillSummary::getDistBizLineCode, BIZ_LINE));
        BigDecimal subPaid = BigDecimal.ZERO;
        BigDecimal subInService = BigDecimal.ZERO;
        for (DistUserBillSummary row : remaining) {
            subPaid = subPaid.add(defaultAmount(row.getDistPaidTotalAmount()));
            subInService = subInService.add(defaultAmount(row.getDistInServiceTotalAmount()));
        }
        promoterRow.setDistSubPaidTotalAmount(subPaid);
        promoterRow.setDistSubInServiceTotalAmount(subInService);
        promoterRow.setUpdateTime(LocalDateTime.now());
        distUserBillSummaryMapper.updateById(promoterRow);
        return true;
    }

    private static BigDecimal defaultAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }
}

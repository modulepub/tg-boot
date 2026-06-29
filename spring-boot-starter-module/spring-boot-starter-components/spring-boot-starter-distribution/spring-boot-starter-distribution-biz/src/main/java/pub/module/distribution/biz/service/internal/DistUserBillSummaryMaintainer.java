package pub.module.distribution.biz.service.internal;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.distribution.api.constants.DistBillEventSourceTypeCodeEnum;
import pub.module.distribution.api.constants.DistBizLineCodeEnum;
import pub.module.distribution.biz.util.DistCommissionCalcUtil;
import pub.module.distribution.biz.util.DistPeriodParseUtil;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.distribution.crud.entity.DistUserBillEvent;
import pub.module.distribution.crud.entity.DistUserBillSettleRecord;
import pub.module.distribution.crud.entity.DistUserBillSummary;
import pub.module.distribution.crud.mapper.DistUserBillEventMapper;
import pub.module.distribution.crud.mapper.DistUserBillSettleRecordMapper;
import pub.module.distribution.crud.mapper.DistUserBillSummaryMapper;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.trade.api.dto.TdOrderGoodsDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 用户账单汇总增量维护（统计数据来源于 trade 支付成功消息）。
 */
@Service
public class DistUserBillSummaryMaintainer {

    private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.9000");

    @Resource
    private DistUserBillSummaryMapper distUserBillSummaryMapper;
    @Resource
    private DistUserBillEventMapper distUserBillEventMapper;
    @Resource
    private DistUserBillSettleRecordMapper distUserBillSettleRecordMapper;
    @Resource
    private ApiSysUserService apiSysUserService;

    @Transactional(rollbackFor = Exception.class)
    public void initOnUserRegistered(UserDTO user, String distBizLineCode) {
        if (user == null || StrUtil.isBlank(user.getUserCode())) {
            return;
        }
        String bizLine = StrUtil.blankToDefault(distBizLineCode, DistBizLineCodeEnum.DATING.getCode());
        loadOrCreate(user.getUserCode(), bizLine);
    }

    @Transactional(rollbackFor = Exception.class)
    public void onOrderGoodsPaid(TdOrderGoodsDTO dto) {
        if (dto == null || StrUtil.isBlank(dto.getTdOdSysUserCode()) || StrUtil.isBlank(dto.getTdOdGdCode())) {
            return;
        }
        BigDecimal amount = dto.getTdOdGdAmount();
        if (isInvalidAmount(amount)) {
            return;
        }
        String payerUserCode = dto.getTdOdSysUserCode();
        String bizLine = DistBizLineCodeEnum.DATING.getCode();
        boolean testPayer = isTestUser(payerUserCode);
        if (!tryClaimBillEvent(bizLine, dto.getTdOdGdCode(), payerUserCode, testPayer)) {
            return;
        }

        boolean inService = DistPeriodParseUtil.hasServicePeriod(dto.getTdGdDayPeriod());
        accumulateSelf(payerUserCode, bizLine, amount, inService);

        String inviterCode = resolveInviterUserCode(payerUserCode);
        if (StrUtil.isNotBlank(inviterCode) && !Objects.equals(inviterCode, payerUserCode)) {
            accumulateInviterSub(inviterCode, bizLine, amount, inService);
        }

        BigDecimal commissionRate = resolveCommissionRate(dto.getTdGdCommissionRate());
        DistCommissionCalcUtil.CommissionBreakdown commission =
                DistCommissionCalcUtil.calc(amount, commissionRate);
        String superiorInviterCode = resolveSuperiorInviterUserCode(inviterCode);

        createSettleRecord(dto, bizLine, payerUserCode, amount,
                inService, commissionRate, commission, inviterCode, superiorInviterCode, testPayer);
    }

    @Transactional(rollbackFor = Exception.class)
    public int settleDueBillRecords() {
        LocalDateTime now = LocalDateTime.now();
        List<DistUserBillSettleRecord> dueList = distUserBillSettleRecordMapper.selectList(
                new QueryWrapper<DistUserBillSettleRecord>().lambda()
                        .eq(DistUserBillSettleRecord::getDistSettledStatusCode, StatusCodeEnum.NO.getCode())
                        .le(DistUserBillSettleRecord::getDistServicePeriodEndAt, now));
        int count = 0;
        for (DistUserBillSettleRecord record : dueList) {
            settleRecord(record, now);
            count++;
        }
        return count;
    }

    private void accumulateSelf(String distUserCode, String distBizLineCode, BigDecimal amount, boolean inService) {
        DistUserBillSummary summary = loadOrCreate(distUserCode, distBizLineCode);
        ensureInviterLinked(summary);
        summary.setDistPaidTotalAmount(safeAdd(summary.getDistPaidTotalAmount(), amount));
        if (inService) {
            summary.setDistInServiceTotalAmount(safeAdd(summary.getDistInServiceTotalAmount(), amount));
        }
        summary.setUpdateTime(LocalDateTime.now());
        distUserBillSummaryMapper.updateById(summary);
    }

    private void accumulateInviterSub(String inviterUserCode, String distBizLineCode, BigDecimal amount, boolean inService) {
        DistUserBillSummary inviterSummary = loadOrCreate(inviterUserCode, distBizLineCode);
        inviterSummary.setDistSubPaidTotalAmount(safeAdd(inviterSummary.getDistSubPaidTotalAmount(), amount));
        if (inService) {
            inviterSummary.setDistSubInServiceTotalAmount(
                    safeAdd(inviterSummary.getDistSubInServiceTotalAmount(), amount));
        }
        inviterSummary.setUpdateTime(LocalDateTime.now());
        distUserBillSummaryMapper.updateById(inviterSummary);
    }

    private DistUserBillSettleRecord createSettleRecord(TdOrderGoodsDTO dto, String bizLine, String payerUserCode,
            BigDecimal amount, boolean inService, BigDecimal commissionRate,
            DistCommissionCalcUtil.CommissionBreakdown commission, String inviterCode,
            String superiorInviterCode, boolean testPayer) {
        LocalDateTime now = LocalDateTime.now();
        DistUserBillSettleRecord record = new DistUserBillSettleRecord();
        record.setId(IdUtil.getSnowflakeNextIdStr());
        record.setDistUserBillSettleRecordCode(IdUtil.getSnowflakeNextIdStr());
        record.setDistBizLineCode(bizLine);
        record.setDistPayerUserCode(payerUserCode);
        record.setTdOdSysUserCode(dto.getTdOdSysUserCode());
        record.setTdOdSysUserRealName(dto.getTdOdSysUserRealName());
        record.setTdOdGdCode(dto.getTdOdGdCode());
        record.setTdGdCode(dto.getTdGdCode());
        record.setTdGdName(dto.getTdGdName());
        record.setTdGdCommissionRate(commissionRate);
        record.setDistPaidAmount(amount);
        record.setDistCommissionPoolAmount(commission.commissionPool());
        record.setDistInviterUserCode(inviterCode);
        record.setDistInviterCommissionAmount(commission.directCommission());
        record.setDistSuperiorInviterUserCode(superiorInviterCode);
        record.setDistSuperiorCommissionAmount(commission.superiorCommission());
        record.setDistCommissionAppliedStatusCode(StatusCodeEnum.NO.getCode());
        record.setDistSettledStatusCode(StatusCodeEnum.NO.getCode());
        record.setDistSettleAppliedStatusCode(StatusCodeEnum.NO.getCode());
        record.setDistInServiceStatusCode(inService ? StatusCodeEnum.YES.getCode() : StatusCodeEnum.NO.getCode());
        if (inService) {
            record.setDistServicePeriodEndAt(DistPeriodParseUtil.resolveEndAt(now, dto.getTdGdDayPeriod()));
        }
        record.setDistTestStatusCode(testFlag(testPayer));
        record.setCreateTime(now);
        record.setUpdateTime(now);
        distUserBillSettleRecordMapper.insert(record);
        return record;
    }

    private void settleRecord(DistUserBillSettleRecord record, LocalDateTime now) {
        record.setDistSettledStatusCode(StatusCodeEnum.YES.getCode());
        record.setDistInServiceStatusCode(StatusCodeEnum.NO.getCode());
        record.setDistSettledAt(now);
        record.setUpdateTime(now);
        distUserBillSettleRecordMapper.updateById(record);

        BigDecimal amount = record.getDistPaidAmount();
        String payerUserCode = record.getDistPayerUserCode();
        String bizLine = record.getDistBizLineCode();
        debitInServiceSelf(payerUserCode, bizLine, amount);

        String inviterCode = resolveInviterUserCode(payerUserCode);
        if (StrUtil.isNotBlank(inviterCode) && !Objects.equals(inviterCode, payerUserCode)) {
            debitInServiceInviterSub(inviterCode, bizLine, amount);
        }
    }

    private void debitInServiceSelf(String distUserCode, String distBizLineCode, BigDecimal amount) {
        DistUserBillSummary summary = loadOrCreate(distUserCode, distBizLineCode);
        summary.setDistInServiceTotalAmount(safeSubtract(summary.getDistInServiceTotalAmount(), amount));
        summary.setUpdateTime(LocalDateTime.now());
        distUserBillSummaryMapper.updateById(summary);
    }

    private void debitInServiceInviterSub(String inviterUserCode, String distBizLineCode, BigDecimal amount) {
        DistUserBillSummary inviterSummary = loadOrCreate(inviterUserCode, distBizLineCode);
        inviterSummary.setDistSubInServiceTotalAmount(
                safeSubtract(inviterSummary.getDistSubInServiceTotalAmount(), amount));
        inviterSummary.setUpdateTime(LocalDateTime.now());
        distUserBillSummaryMapper.updateById(inviterSummary);
    }

    private String resolveInviterUserCode(String payerUserCode) {
        DistUserBillSummary payerSummary = distUserBillSummaryMapper.selectOne(new QueryWrapper<DistUserBillSummary>().lambda()
                .eq(DistUserBillSummary::getDistUserCode, payerUserCode)
                .eq(DistUserBillSummary::getDistBizLineCode, DistBizLineCodeEnum.DATING.getCode()), false);
        if (payerSummary != null && StrUtil.isNotBlank(payerSummary.getDistInviterUserCode())) {
            return payerSummary.getDistInviterUserCode();
        }
        UserDTO payer = apiSysUserService.getUserByUserCode(payerUserCode);
        if (payer == null) {
            return null;
        }
        return StrUtil.trimToNull(payer.getUserReferenceUserCode());
    }

    private String resolveSuperiorInviterUserCode(String directInviterUserCode) {
        if (StrUtil.isBlank(directInviterUserCode)) {
            return null;
        }
        return resolveInviterUserCode(directInviterUserCode);
    }

    private void ensureInviterLinked(DistUserBillSummary summary) {
        if (StrUtil.isNotBlank(summary.getDistInviterUserCode())) {
            return;
        }
        refreshNickNames(summary);
        distUserBillSummaryMapper.updateById(summary);
    }

    private boolean tryClaimBillEvent(String bizLine, String sourceId, String payerUserCode, boolean testPayer) {
        LocalDateTime now = LocalDateTime.now();
        DistUserBillEvent event = new DistUserBillEvent();
        event.setId(IdUtil.getSnowflakeNextIdStr());
        event.setDistUserBillEventCode(IdUtil.getSnowflakeNextIdStr());
        event.setDistBizLineCode(bizLine);
        event.setDistBillEventSourceTypeCode(DistBillEventSourceTypeCodeEnum.ORDER_PAID.getCode());
        event.setDistBillEventSourceId(sourceId);
        event.setDistPayerUserCode(payerUserCode);
        event.setDistTestStatusCode(testFlag(testPayer));
        event.setCreateTime(now);
        event.setUpdateTime(now);
        try {
            distUserBillEventMapper.insert(event);
            return true;
        } catch (DuplicateKeyException ex) {
            return false;
        }
    }

    private DistUserBillSummary loadOrCreate(String distUserCode, String distBizLineCode) {
        DistUserBillSummary existing = distUserBillSummaryMapper.selectOne(new QueryWrapper<DistUserBillSummary>().lambda()
                .eq(DistUserBillSummary::getDistUserCode, distUserCode)
                .eq(DistUserBillSummary::getDistBizLineCode, distBizLineCode), false);
        if (existing != null) {
            return existing;
        }
        LocalDateTime now = LocalDateTime.now();
        DistUserBillSummary summary = new DistUserBillSummary();
        summary.setId(IdUtil.getSnowflakeNextIdStr());
        summary.setDistUserBillSummaryCode(IdUtil.getSnowflakeNextIdStr());
        summary.setDistUserCode(distUserCode);
        summary.setDistBizLineCode(distBizLineCode);
        summary.setDistPaidTotalAmount(BigDecimal.ZERO);
        summary.setDistInServiceTotalAmount(BigDecimal.ZERO);
        summary.setDistSubPaidTotalAmount(BigDecimal.ZERO);
        summary.setDistSubInServiceTotalAmount(BigDecimal.ZERO);
        summary.setDistTestStatusCode(testFlag(isTestUser(distUserCode)));
        summary.setCreateTime(now);
        summary.setUpdateTime(now);
        refreshNickNames(summary);
        distUserBillSummaryMapper.insert(summary);
        return summary;
    }

    private boolean isTestUser(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return false;
        }
        UserDTO user = apiSysUserService.getUserByUserCode(userCode);
        return user != null && StatusCodeEnum.YES.equals(user.getUserTestStatusCode());
    }

    private static String testFlag(boolean testData) {
        return testData ? StatusCodeEnum.YES.getCode() : StatusCodeEnum.NO.getCode();
    }

    private void refreshNickNames(DistUserBillSummary summary) {
        UserDTO user = apiSysUserService.getUserByUserCode(summary.getDistUserCode());
        summary.setDistUserNickName(user != null ? user.getUserNickName() : summary.getDistUserCode());
        summary.setDistUserRealName(user != null ? StrUtil.trimToNull(user.getUserRealName()) : null);
        String inviterCode = user != null ? StrUtil.trimToNull(user.getUserReferenceUserCode()) : null;
        summary.setDistInviterUserCode(inviterCode);
        if (StrUtil.isBlank(inviterCode)) {
            summary.setDistInviterUserNickName(null);
            summary.setDistInviterUserRealName(null);
            return;
        }
        UserDTO inviter = apiSysUserService.getUserByUserCode(inviterCode);
        summary.setDistInviterUserNickName(inviter != null ? inviter.getUserNickName() : inviterCode);
        summary.setDistInviterUserRealName(inviter != null ? StrUtil.trimToNull(inviter.getUserRealName()) : null);
    }

    private static boolean isInvalidAmount(BigDecimal amount) {
        return amount == null || amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    private static BigDecimal resolveCommissionRate(BigDecimal rate) {
        return rate != null ? rate : DEFAULT_COMMISSION_RATE;
    }

    private static BigDecimal safeAdd(BigDecimal left, BigDecimal right) {
        BigDecimal base = Objects.requireNonNullElse(left, BigDecimal.ZERO);
        BigDecimal delta = Objects.requireNonNullElse(right, BigDecimal.ZERO);
        return base.add(delta);
    }

    private static BigDecimal safeSubtract(BigDecimal left, BigDecimal right) {
        BigDecimal base = Objects.requireNonNullElse(left, BigDecimal.ZERO);
        BigDecimal delta = Objects.requireNonNullElse(right, BigDecimal.ZERO);
        BigDecimal result = base.subtract(delta);
        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
    }
}


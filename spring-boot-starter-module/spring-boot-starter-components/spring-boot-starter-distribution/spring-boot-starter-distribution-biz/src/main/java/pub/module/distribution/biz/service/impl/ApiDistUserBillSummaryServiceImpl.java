package pub.module.distribution.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.distribution.api.constants.DistBizLineCodeEnum;
import pub.module.distribution.api.service.ApiDistUserBillSummaryService;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.distribution.api.service.dto.DistEnterpriseBillStatsDTO;
import pub.module.distribution.api.service.dto.DistStaffSettleCustomerDTO;
import pub.module.distribution.api.service.dto.DistUserBillPromoteStatsDTO;
import pub.module.distribution.api.service.dto.DistUserBillSettleRecordDTO;
import pub.module.distribution.api.service.dto.DistUserBillSummaryDTO;
import pub.module.distribution.biz.service.internal.DistUserBillSummaryMaintainer;
import pub.module.distribution.crud.entity.DistUserBillSettleRecord;
import pub.module.distribution.crud.entity.DistUserBillSummary;
import pub.module.distribution.crud.mapper.DistUserBillSettleRecordMapper;
import pub.module.distribution.crud.mapper.DistUserBillSummaryMapper;
import pub.module.system.api.service.dto.UserDTO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class ApiDistUserBillSummaryServiceImpl implements ApiDistUserBillSummaryService {

    @Resource
    private DistUserBillSummaryMapper distUserBillSummaryMapper;
    @Resource
    private DistUserBillSettleRecordMapper distUserBillSettleRecordMapper;
    @Resource
    private DistUserBillSummaryMaintainer distUserBillSummaryMaintainer;

    @Override
    public DistUserBillSummaryDTO getSummary(String distUserCode, String distBizLineCode) {
        String bizLine = resolveBizLine(distBizLineCode);
        DistUserBillSummary summary = distUserBillSummaryMapper.selectOne(new QueryWrapper<DistUserBillSummary>().lambda()
                .eq(DistUserBillSummary::getDistUserCode, distUserCode)
                .eq(DistUserBillSummary::getDistBizLineCode, bizLine), false);
        if (summary == null) {
            return emptySummary(distUserCode, bizLine);
        }
        return toDto(summary);
    }

    @Override
    public void initOnUserRegistered(UserDTO user, String distBizLineCode) {
        distUserBillSummaryMaintainer.initOnUserRegistered(user, distBizLineCode);
    }

    @Override
    public DistUserBillPromoteStatsDTO getPromoteStats(String distInviterUserCode, String distBizLineCode) {
        String bizLine = resolveBizLine(distBizLineCode);
        DistUserBillPromoteStatsDTO stats = new DistUserBillPromoteStatsDTO();
        long inviteeCount = distUserBillSummaryMapper.selectCount(new QueryWrapper<DistUserBillSummary>().lambda()
                .eq(DistUserBillSummary::getDistInviterUserCode, distInviterUserCode)
                .eq(DistUserBillSummary::getDistBizLineCode, bizLine));
        stats.setDistInviteeCount(inviteeCount);

        DistUserBillSummary promoterRow = distUserBillSummaryMapper.selectOne(new QueryWrapper<DistUserBillSummary>().lambda()
                .eq(DistUserBillSummary::getDistUserCode, distInviterUserCode)
                .eq(DistUserBillSummary::getDistBizLineCode, bizLine), false);
        if (promoterRow == null) {
            stats.setDistSubPaidTotalAmount(BigDecimal.ZERO);
            stats.setDistSubInServiceTotalAmount(BigDecimal.ZERO);
        } else {
            stats.setDistSubPaidTotalAmount(defaultAmount(promoterRow.getDistSubPaidTotalAmount()));
            stats.setDistSubInServiceTotalAmount(defaultAmount(promoterRow.getDistSubInServiceTotalAmount()));
        }
        return stats;
    }

    @Override
    public IPage<DistUserBillSummaryDTO> pageByPromoter(String distInviterUserCode, String distBizLineCode,
            int pageNo, int pageSize) {
        String bizLine = resolveBizLine(distBizLineCode);
        int safePageNo = pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize < 1 ? 10 : Math.min(pageSize, 50);
        Page<DistUserBillSummary> page = new Page<>(safePageNo, safePageSize);
        distUserBillSummaryMapper.selectPage(page, new QueryWrapper<DistUserBillSummary>().lambda()
                .eq(DistUserBillSummary::getDistInviterUserCode, distInviterUserCode)
                .eq(DistUserBillSummary::getDistBizLineCode, bizLine)
                .orderByDesc(DistUserBillSummary::getCreateTime));
        Page<DistUserBillSummaryDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(ApiDistUserBillSummaryServiceImpl::toDto).toList());
        return result;
    }

    @Override
    public DistEnterpriseBillStatsDTO getStatsByUserCodes(List<String> distUserCodes, String distBizLineCode) {
        String bizLine = resolveBizLine(distBizLineCode);
        DistEnterpriseBillStatsDTO stats = new DistEnterpriseBillStatsDTO();
        List<String> userCodes = distUserCodes == null ? List.of()
                : distUserCodes.stream().filter(StrUtil::isNotBlank).map(String::trim).distinct().toList();
        stats.setDistStaffCount((long) userCodes.size());
        if (userCodes.isEmpty()) {
            stats.setDistPaidTotalAmount(BigDecimal.ZERO);
            stats.setDistInServiceTotalAmount(BigDecimal.ZERO);
            stats.setDistSubPaidTotalAmount(BigDecimal.ZERO);
            stats.setDistSubInServiceTotalAmount(BigDecimal.ZERO);
            return stats;
        }
        List<DistUserBillSummary> rows = distUserBillSummaryMapper.selectList(new QueryWrapper<DistUserBillSummary>().lambda()
                .in(DistUserBillSummary::getDistUserCode, userCodes)
                .eq(DistUserBillSummary::getDistBizLineCode, bizLine));
        stats.setDistPaidTotalAmount(sumAmount(rows, DistUserBillSummary::getDistPaidTotalAmount));
        stats.setDistInServiceTotalAmount(sumAmount(rows, DistUserBillSummary::getDistInServiceTotalAmount));
        stats.setDistSubPaidTotalAmount(sumAmount(rows, DistUserBillSummary::getDistSubPaidTotalAmount));
        stats.setDistSubInServiceTotalAmount(sumAmount(rows, DistUserBillSummary::getDistSubInServiceTotalAmount));
        return stats;
    }

    @Override
    public IPage<DistUserBillSummaryDTO> pageByUserCodes(List<String> distUserCodes, String distBizLineCode,
            int pageNo, int pageSize) {
        String bizLine = resolveBizLine(distBizLineCode);
        int safePageNo = pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize < 1 ? 10 : Math.min(pageSize, 50);
        List<String> userCodes = distUserCodes == null ? List.of()
                : distUserCodes.stream().filter(StrUtil::isNotBlank).map(String::trim).distinct().toList();
        Page<DistUserBillSummaryDTO> empty = new Page<>(safePageNo, safePageSize, 0);
        empty.setRecords(List.of());
        if (userCodes.isEmpty()) {
            return empty;
        }
        Page<DistUserBillSummary> page = new Page<>(safePageNo, safePageSize);
        distUserBillSummaryMapper.selectPage(page, new QueryWrapper<DistUserBillSummary>().lambda()
                .in(DistUserBillSummary::getDistUserCode, userCodes)
                .eq(DistUserBillSummary::getDistBizLineCode, bizLine)
                .orderByDesc(DistUserBillSummary::getCreateTime));
        Page<DistUserBillSummaryDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(ApiDistUserBillSummaryServiceImpl::toDto).toList());
        return result;
    }

    private static BigDecimal sumAmount(List<DistUserBillSummary> rows,
            Function<DistUserBillSummary, BigDecimal> getter) {
        return rows.stream()
                .map(getter)
                .map(ApiDistUserBillSummaryServiceImpl::defaultAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static String resolveBizLine(String distBizLineCode) {
        return StrUtil.blankToDefault(distBizLineCode, DistBizLineCodeEnum.DATING.getCode());
    }

    private static BigDecimal defaultAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    private static DistUserBillSummaryDTO emptySummary(String distUserCode, String bizLine) {
        DistUserBillSummaryDTO dto = new DistUserBillSummaryDTO();
        dto.setDistUserCode(distUserCode);
        dto.setDistBizLineCode(bizLine);
        dto.setDistPaidTotalAmount(BigDecimal.ZERO);
        dto.setDistInServiceTotalAmount(BigDecimal.ZERO);
        dto.setDistSubPaidTotalAmount(BigDecimal.ZERO);
        dto.setDistSubInServiceTotalAmount(BigDecimal.ZERO);
        return dto;
    }

    private static DistUserBillSummaryDTO toDto(DistUserBillSummary summary) {
        DistUserBillSummaryDTO dto = new DistUserBillSummaryDTO();
        dto.setId(summary.getId());
        dto.setCreateBy(summary.getCreateBy());
        dto.setCreateTime(summary.getCreateTime());
        dto.setUpdateBy(summary.getUpdateBy());
        dto.setUpdateTime(summary.getUpdateTime());
        dto.setOrgCode(summary.getOrgCode());
        dto.setVersion(summary.getVersion());
        dto.setSeqNo(summary.getSeqNo());
        dto.setDeleted(summary.getDeleted());
        dto.setDistUserBillSummaryCode(summary.getDistUserBillSummaryCode());
        dto.setDistUserCode(summary.getDistUserCode());
        dto.setDistUserNickName(summary.getDistUserNickName());
        dto.setDistUserRealName(summary.getDistUserRealName());
        dto.setDistInviterUserCode(summary.getDistInviterUserCode());
        dto.setDistInviterUserNickName(summary.getDistInviterUserNickName());
        dto.setDistInviterUserRealName(summary.getDistInviterUserRealName());
        dto.setDistBizLineCode(summary.getDistBizLineCode());
        dto.setDistPaidTotalAmount(summary.getDistPaidTotalAmount());
        dto.setDistInServiceTotalAmount(summary.getDistInServiceTotalAmount());
        dto.setDistSubPaidTotalAmount(summary.getDistSubPaidTotalAmount());
        dto.setDistSubInServiceTotalAmount(summary.getDistSubInServiceTotalAmount());
        return dto;
    }

    @Override
    public int settleDueBillRecords() {
        return distUserBillSummaryMaintainer.settleDueBillRecords();
    }

    @Override
    public IPage<DistUserBillSettleRecordDTO> pageSettleRecordsByInvitee(String distInviterUserCode,
            String distPayerUserCode, String distBizLineCode, int pageNo, int pageSize) {
        String bizLine = resolveBizLine(distBizLineCode);
        int safePageNo = pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize < 1 ? 10 : Math.min(pageSize, 50);
        Page<DistUserBillSettleRecordDTO> empty = new Page<>(safePageNo, safePageSize, 0);
        empty.setRecords(List.of());
        if (StrUtil.isBlank(distInviterUserCode) || StrUtil.isBlank(distPayerUserCode)) {
            return empty;
        }
        if (!isInviteeOfPromoter(distInviterUserCode, distPayerUserCode, bizLine)) {
            return empty;
        }
        Page<DistUserBillSettleRecord> page = new Page<>(safePageNo, safePageSize);
        distUserBillSettleRecordMapper.selectPage(page, new QueryWrapper<DistUserBillSettleRecord>().lambda()
                .eq(DistUserBillSettleRecord::getDistPayerUserCode, distPayerUserCode)
                .eq(DistUserBillSettleRecord::getDistBizLineCode, bizLine)
                .orderByDesc(DistUserBillSettleRecord::getCreateTime));
        Page<DistUserBillSettleRecordDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(ApiDistUserBillSummaryServiceImpl::toSettleDto).toList());
        return result;
    }

    @Override
    public IPage<DistUserBillSettleRecordDTO> pageSettleRecordsByPayer(String distPayerUserCode, String distBizLineCode,
            int pageNo, int pageSize) {
        String bizLine = resolveBizLine(distBizLineCode);
        int safePageNo = pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize < 1 ? 10 : Math.min(pageSize, 50);
        Page<DistUserBillSettleRecordDTO> empty = new Page<>(safePageNo, safePageSize, 0);
        empty.setRecords(List.of());
        if (StrUtil.isBlank(distPayerUserCode)) {
            return empty;
        }
        Page<DistUserBillSettleRecord> page = new Page<>(safePageNo, safePageSize);
        distUserBillSettleRecordMapper.selectPage(page, new QueryWrapper<DistUserBillSettleRecord>().lambda()
                .eq(DistUserBillSettleRecord::getDistPayerUserCode, distPayerUserCode.trim())
                .eq(DistUserBillSettleRecord::getDistBizLineCode, bizLine)
                .orderByDesc(DistUserBillSettleRecord::getCreateTime));
        Page<DistUserBillSettleRecordDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(ApiDistUserBillSummaryServiceImpl::toSettleDto).toList());
        return result;
    }

    @Override
    public List<String> resolveStaffSettlePayerUserCodes(String distMatchmakerUserCode, String distBizLineCode) {
        String bizLine = resolveBizLine(distBizLineCode);
        String matchmakerCode = StrUtil.trim(distMatchmakerUserCode);
        if (StrUtil.isBlank(matchmakerCode)) {
            return List.of();
        }
        return distUserBillSummaryMapper.selectList(new QueryWrapper<DistUserBillSummary>().lambda()
                        .eq(DistUserBillSummary::getDistBizLineCode, bizLine)
                        .eq(DistUserBillSummary::getDistInviterUserCode, matchmakerCode)
                        .select(DistUserBillSummary::getDistUserCode))
                .stream()
                .map(DistUserBillSummary::getDistUserCode)
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .distinct()
                .toList();
    }

    @Override
    public IPage<DistStaffSettleCustomerDTO> pageStaffSettleCustomers(String distMatchmakerUserCode,
            String distBizLineCode, int pageNo, int pageSize) {
        String bizLine = resolveBizLine(distBizLineCode);
        String matchmakerCode = StrUtil.trim(distMatchmakerUserCode);
        int safePageNo = pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize < 1 ? 10 : Math.min(pageSize, 50);
        Page<DistStaffSettleCustomerDTO> empty = new Page<>(safePageNo, safePageSize, 0);
        empty.setRecords(List.of());
        if (StrUtil.isBlank(matchmakerCode)) {
            return empty;
        }
        Page<DistUserBillSummary> page = new Page<>(safePageNo, safePageSize);
        distUserBillSummaryMapper.selectPage(page, new QueryWrapper<DistUserBillSummary>().lambda()
                .eq(DistUserBillSummary::getDistInviterUserCode, matchmakerCode)
                .eq(DistUserBillSummary::getDistBizLineCode, bizLine)
                .orderByDesc(DistUserBillSummary::getCreateTime));
        List<String> payerCodes = page.getRecords().stream()
                .map(DistUserBillSummary::getDistUserCode)
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .distinct()
                .toList();
        Map<String, BigDecimal> commissionMap = sumUnsettledCommissionByPayer(matchmakerCode, bizLine, payerCodes);
        Page<DistStaffSettleCustomerDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(summary -> {
            DistStaffSettleCustomerDTO dto = new DistStaffSettleCustomerDTO();
            dto.setDistUserCode(summary.getDistUserCode());
            dto.setDistUserNickName(summary.getDistUserNickName());
            dto.setDistUserRealName(summary.getDistUserRealName());
            dto.setDistPaidTotalAmount(defaultAmount(summary.getDistPaidTotalAmount()));
            dto.setDistInServiceTotalAmount(defaultAmount(summary.getDistInServiceTotalAmount()));
            String payerCode = StrUtil.trim(summary.getDistUserCode());
            dto.setDistUnsettledCommissionAmount(commissionMap.getOrDefault(payerCode, BigDecimal.ZERO));
            return dto;
        }).toList());
        return result;
    }

    @Override
    public BigDecimal sumStaffUnsettledCommission(String distMatchmakerUserCode, String distBizLineCode) {
        String bizLine = resolveBizLine(distBizLineCode);
        String matchmakerCode = StrUtil.trim(distMatchmakerUserCode);
        if (StrUtil.isBlank(matchmakerCode)) {
            return BigDecimal.ZERO;
        }
        List<DistUserBillSettleRecord> records = distUserBillSettleRecordMapper.selectList(
                buildStaffUnsettledQuery(matchmakerCode, bizLine, null));
        return records.stream()
                .map(DistUserBillSettleRecord::getDistInviterCommissionAmount)
                .map(ApiDistUserBillSummaryServiceImpl::defaultAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<String, BigDecimal> sumUnsettledCommissionByPayer(String matchmakerCode, String bizLine,
            List<String> payerCodes) {
        if (payerCodes == null || payerCodes.isEmpty()) {
            return Map.of();
        }
        List<DistUserBillSettleRecord> records = distUserBillSettleRecordMapper.selectList(
                buildStaffUnsettledQuery(matchmakerCode, bizLine, payerCodes));
        Map<String, BigDecimal> result = new HashMap<>();
        for (DistUserBillSettleRecord record : records) {
            String payerCode = StrUtil.trim(record.getDistPayerUserCode());
            if (StrUtil.isBlank(payerCode)) {
                continue;
            }
            result.merge(payerCode, defaultAmount(record.getDistInviterCommissionAmount()), BigDecimal::add);
        }
        return result;
    }

    private QueryWrapper<DistUserBillSettleRecord> buildStaffUnsettledQuery(String matchmakerCode, String bizLine,
            List<String> payerCodes) {
        QueryWrapper<DistUserBillSettleRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DistUserBillSettleRecord::getDistInviterUserCode, matchmakerCode)
                .eq(DistUserBillSettleRecord::getDistBizLineCode, bizLine)
                .eq(DistUserBillSettleRecord::getDistSettledStatusCode, StatusCodeEnum.NO.getCode())
                .eq(DistUserBillSettleRecord::getDistInServiceStatusCode, StatusCodeEnum.NO.getCode())
                .isNull(DistUserBillSettleRecord::getDistSettleBatchCode);
        if (payerCodes != null && !payerCodes.isEmpty()) {
            queryWrapper.lambda().in(DistUserBillSettleRecord::getDistPayerUserCode, payerCodes);
        }
        return queryWrapper;
    }

    @Override
    public List<String> resolveSettlePayerUserCodes(List<String> distMatchmakerUserCodes, String distBizLineCode) {
        String bizLine = resolveBizLine(distBizLineCode);
        List<String> matchmakerCodes = distMatchmakerUserCodes == null ? List.of()
                : distMatchmakerUserCodes.stream().filter(StrUtil::isNotBlank).map(String::trim).distinct().toList();
        if (matchmakerCodes.isEmpty()) {
            return List.of();
        }
        java.util.LinkedHashSet<String> payerCodes = new java.util.LinkedHashSet<>(matchmakerCodes);
        List<DistUserBillSummary> invitees = distUserBillSummaryMapper.selectList(new QueryWrapper<DistUserBillSummary>().lambda()
                .eq(DistUserBillSummary::getDistBizLineCode, bizLine)
                .in(DistUserBillSummary::getDistInviterUserCode, matchmakerCodes)
                .select(DistUserBillSummary::getDistUserCode));
        for (DistUserBillSummary invitee : invitees) {
            if (StrUtil.isNotBlank(invitee.getDistUserCode())) {
                payerCodes.add(invitee.getDistUserCode().trim());
            }
        }
        return List.copyOf(payerCodes);
    }

    private boolean isInviteeOfPromoter(String distInviterUserCode, String distPayerUserCode, String bizLine) {
        long count = distUserBillSummaryMapper.selectCount(new QueryWrapper<DistUserBillSummary>().lambda()
                .eq(DistUserBillSummary::getDistUserCode, distPayerUserCode)
                .eq(DistUserBillSummary::getDistInviterUserCode, distInviterUserCode)
                .eq(DistUserBillSummary::getDistBizLineCode, bizLine));
        return count > 0;
    }

    private static DistUserBillSettleRecordDTO toSettleDto(DistUserBillSettleRecord record) {
        DistUserBillSettleRecordDTO dto = new DistUserBillSettleRecordDTO();
        dto.setId(record.getId());
        dto.setCreateTime(record.getCreateTime());
        dto.setDistUserBillSettleRecordCode(record.getDistUserBillSettleRecordCode());
        dto.setDistBizLineCode(record.getDistBizLineCode());
        dto.setDistPayerUserCode(record.getDistPayerUserCode());
        dto.setTdOdSysUserCode(record.getTdOdSysUserCode());
        dto.setTdOdSysUserRealName(record.getTdOdSysUserRealName());
        dto.setTdOdGdCode(record.getTdOdGdCode());
        dto.setTdGdCode(record.getTdGdCode());
        dto.setTdGdName(record.getTdGdName());
        dto.setTdGdCommissionRate(record.getTdGdCommissionRate());
        dto.setDistPaidAmount(record.getDistPaidAmount());
        dto.setDistCommissionPoolAmount(record.getDistCommissionPoolAmount());
        dto.setDistInviterUserCode(record.getDistInviterUserCode());
        dto.setDistInviterCommissionAmount(record.getDistInviterCommissionAmount());
        dto.setDistSuperiorInviterUserCode(record.getDistSuperiorInviterUserCode());
        dto.setDistSuperiorCommissionAmount(record.getDistSuperiorCommissionAmount());
        dto.setDistCommissionAppliedStatusCode(record.getDistCommissionAppliedStatusCode());
        dto.setDistSettledStatusCode(record.getDistSettledStatusCode());
        dto.setDistSettleAppliedStatusCode(record.getDistSettleAppliedStatusCode());
        dto.setDistInServiceStatusCode(record.getDistInServiceStatusCode());
        dto.setDistServicePeriodEndAt(record.getDistServicePeriodEndAt());
        dto.setDistSettledAt(record.getDistSettledAt());
        dto.setDistSettleBatchCode(record.getDistSettleBatchCode());
        dto.setDistSettleBatchStatusCode(record.getDistSettleBatchStatusCode());
        return dto;
    }
}

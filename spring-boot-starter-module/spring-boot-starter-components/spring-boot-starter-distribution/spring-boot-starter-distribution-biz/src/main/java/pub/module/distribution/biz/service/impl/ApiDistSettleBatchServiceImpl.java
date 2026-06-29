package pub.module.distribution.biz.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.distribution.api.constants.DistBizLineCodeEnum;
import pub.module.distribution.api.service.ApiDistSettleBatchService;
import pub.module.distribution.api.service.dto.DistSettleBatchDTO;
import pub.module.distribution.crud.entity.DistSettleBatch;
import pub.module.distribution.crud.entity.DistUserBillSettleRecord;
import pub.module.distribution.crud.mapper.DistSettleBatchMapper;
import pub.module.distribution.crud.mapper.DistUserBillSettleRecordMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ApiDistSettleBatchServiceImpl implements ApiDistSettleBatchService {

    @Resource
    private DistSettleBatchMapper distSettleBatchMapper;
    @Resource
    private DistUserBillSettleRecordMapper distUserBillSettleRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DistSettleBatchDTO apply(String mkCompanyCode, String mkCompanyName, String mkCompanyAdminUserCode,
            String distBizLineCode, BigDecimal distSettleTotalAmount, List<String> distPayerUserCodes) {
        if (StrUtil.isBlank(mkCompanyCode)) {
            throw new IllegalArgumentException("企业编码不能为空");
        }
        String bizLine = StrUtil.blankToDefault(distBizLineCode, DistBizLineCodeEnum.DATING.getCode());
        BigDecimal amount = defaultAmount(distSettleTotalAmount);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("暂无可结算金额");
        }
        long pendingCount = distSettleBatchMapper.selectCount(new QueryWrapper<DistSettleBatch>().lambda()
                .eq(DistSettleBatch::getMkCompanyCode, mkCompanyCode.trim())
                .eq(DistSettleBatch::getDistBizLineCode, bizLine)
                .eq(DistSettleBatch::getDistSettledStatusCode, StatusCodeEnum.NO.getCode()));
        if (pendingCount > 0) {
            throw new IllegalArgumentException("存在未完成的结算申请，请等待平台处理");
        }

        LocalDateTime now = LocalDateTime.now();
        DistSettleBatch batch = new DistSettleBatch();
        batch.setId(IdUtil.getSnowflakeNextIdStr());
        batch.setDistSettleBatchCode(IdUtil.getSnowflakeNextIdStr());
        batch.setDistBizLineCode(bizLine);
        batch.setMkCompanyCode(mkCompanyCode.trim());
        batch.setMkCompanyName(StrUtil.trim(mkCompanyName));
        batch.setMkCompanyAdminUserCode(StrUtil.trim(mkCompanyAdminUserCode));
        batch.setDistApplyAt(now);
        batch.setDistSettledStatusCode(StatusCodeEnum.NO.getCode());
        batch.setDistSettleTotalAmount(amount);
        batch.setCreateTime(now);
        batch.setUpdateTime(now);
        distSettleBatchMapper.insert(batch);
        linkSettleRecords(batch.getDistSettleBatchCode(), bizLine, distPayerUserCodes, now);
        return toDto(batch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DistSettleBatchDTO applyForStaff(String mkCompanyCode, String mkCompanyName, String mkCompanyAdminUserCode,
            String distMatchmakerUserCode, String distBizLineCode, BigDecimal distSettleTotalAmount,
            List<String> distPayerUserCodes) {
        if (StrUtil.isBlank(mkCompanyCode)) {
            throw new IllegalArgumentException("企业编码不能为空");
        }
        String matchmakerCode = StrUtil.trim(distMatchmakerUserCode);
        if (StrUtil.isBlank(matchmakerCode)) {
            throw new IllegalArgumentException("红娘用户编码不能为空");
        }
        String bizLine = StrUtil.blankToDefault(distBizLineCode, DistBizLineCodeEnum.DATING.getCode());
        BigDecimal amount = defaultAmount(distSettleTotalAmount);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("暂无可结算金额");
        }
        long pendingCount = distSettleBatchMapper.selectCount(new QueryWrapper<DistSettleBatch>().lambda()
                .eq(DistSettleBatch::getMkCompanyCode, mkCompanyCode.trim())
                .eq(DistSettleBatch::getDistMatchmakerUserCode, matchmakerCode)
                .eq(DistSettleBatch::getDistBizLineCode, bizLine)
                .eq(DistSettleBatch::getDistSettledStatusCode, StatusCodeEnum.NO.getCode()));
        if (pendingCount > 0) {
            throw new IllegalArgumentException("该红娘存在未完成的结算申请，请等待平台处理");
        }

        LocalDateTime now = LocalDateTime.now();
        DistSettleBatch batch = new DistSettleBatch();
        batch.setId(IdUtil.getSnowflakeNextIdStr());
        batch.setDistSettleBatchCode(IdUtil.getSnowflakeNextIdStr());
        batch.setDistBizLineCode(bizLine);
        batch.setMkCompanyCode(mkCompanyCode.trim());
        batch.setMkCompanyName(StrUtil.trim(mkCompanyName));
        batch.setMkCompanyAdminUserCode(StrUtil.trim(mkCompanyAdminUserCode));
        batch.setDistMatchmakerUserCode(matchmakerCode);
        batch.setDistApplyAt(now);
        batch.setDistSettledStatusCode(StatusCodeEnum.NO.getCode());
        batch.setDistSettleTotalAmount(amount);
        batch.setCreateTime(now);
        batch.setUpdateTime(now);
        distSettleBatchMapper.insert(batch);
        linkStaffSettleRecords(batch.getDistSettleBatchCode(), bizLine, matchmakerCode, distPayerUserCodes, now);
        return toDto(batch);
    }

    @Override
    public IPage<DistSettleBatchDTO> pageByCompanyAndStaff(String mkCompanyCode, String distMatchmakerUserCode,
            String distBizLineCode, int pageNo, int pageSize) {
        String bizLine = StrUtil.blankToDefault(distBizLineCode, DistBizLineCodeEnum.DATING.getCode());
        int safePageNo = pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize < 1 ? 10 : Math.min(pageSize, 50);
        Page<DistSettleBatchDTO> empty = new Page<>(safePageNo, safePageSize, 0);
        empty.setRecords(java.util.List.of());
        if (StrUtil.isBlank(mkCompanyCode) || StrUtil.isBlank(distMatchmakerUserCode)) {
            return empty;
        }
        Page<DistSettleBatch> page = new Page<>(safePageNo, safePageSize);
        distSettleBatchMapper.selectPage(page, new QueryWrapper<DistSettleBatch>().lambda()
                .eq(DistSettleBatch::getMkCompanyCode, mkCompanyCode.trim())
                .eq(DistSettleBatch::getDistMatchmakerUserCode, distMatchmakerUserCode.trim())
                .eq(DistSettleBatch::getDistBizLineCode, bizLine)
                .orderByDesc(DistSettleBatch::getDistApplyAt));
        Page<DistSettleBatchDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(ApiDistSettleBatchServiceImpl::toDto).toList());
        return result;
    }

    @Override
    public IPage<DistSettleBatchDTO> pageByCompany(String mkCompanyCode, String distBizLineCode,
            int pageNo, int pageSize) {
        String bizLine = StrUtil.blankToDefault(distBizLineCode, DistBizLineCodeEnum.DATING.getCode());
        int safePageNo = pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize < 1 ? 10 : Math.min(pageSize, 50);
        Page<DistSettleBatchDTO> empty = new Page<>(safePageNo, safePageSize, 0);
        empty.setRecords(java.util.List.of());
        if (StrUtil.isBlank(mkCompanyCode)) {
            return empty;
        }
        Page<DistSettleBatch> page = new Page<>(safePageNo, safePageSize);
        distSettleBatchMapper.selectPage(page, new QueryWrapper<DistSettleBatch>().lambda()
                .eq(DistSettleBatch::getMkCompanyCode, mkCompanyCode.trim())
                .eq(DistSettleBatch::getDistBizLineCode, bizLine)
                .orderByDesc(DistSettleBatch::getDistApplyAt));
        Page<DistSettleBatchDTO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(ApiDistSettleBatchServiceImpl::toDto).toList());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(String id, String distSettleBatchCode) {
        DistSettleBatch batch = resolveBatch(id, distSettleBatchCode);
        if (batch == null) {
            throw new IllegalArgumentException("结算批次不存在");
        }
        if (StatusCodeEnum.YES.getCode().equals(batch.getDistSettledStatusCode())) {
            throw new IllegalArgumentException("该批次已完成结算");
        }
        LocalDateTime now = LocalDateTime.now();
        batch.setDistSettledStatusCode(StatusCodeEnum.YES.getCode());
        batch.setDistSettledAt(now);
        batch.setUpdateTime(now);
        distSettleBatchMapper.updateById(batch);
        syncSettleRecordsOnBatchComplete(batch.getDistSettleBatchCode(), now);
    }

    private void linkStaffSettleRecords(String batchCode, String bizLine, String matchmakerUserCode,
            List<String> payerUserCodes, LocalDateTime now) {
        if (StrUtil.isBlank(batchCode) || StrUtil.isBlank(matchmakerUserCode) || payerUserCodes == null
                || payerUserCodes.isEmpty()) {
            return;
        }
        List<String> codes = payerUserCodes.stream()
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .distinct()
                .toList();
        if (codes.isEmpty()) {
            return;
        }
        DistUserBillSettleRecord update = new DistUserBillSettleRecord();
        update.setDistSettleBatchCode(batchCode);
        update.setDistSettleBatchStatusCode(StatusCodeEnum.NO.getCode());
        update.setDistSettleAppliedStatusCode(StatusCodeEnum.YES.getCode());
        update.setUpdateTime(now);
        distUserBillSettleRecordMapper.update(update, new UpdateWrapper<DistUserBillSettleRecord>().lambda()
                .in(DistUserBillSettleRecord::getDistPayerUserCode, codes)
                .eq(DistUserBillSettleRecord::getDistInviterUserCode, matchmakerUserCode.trim())
                .eq(DistUserBillSettleRecord::getDistBizLineCode, bizLine)
                .eq(DistUserBillSettleRecord::getDistSettledStatusCode, StatusCodeEnum.NO.getCode())
                .eq(DistUserBillSettleRecord::getDistInServiceStatusCode, StatusCodeEnum.NO.getCode())
                .isNull(DistUserBillSettleRecord::getDistSettleBatchCode));
    }

    private void linkSettleRecords(String batchCode, String bizLine, List<String> payerUserCodes, LocalDateTime now) {
        if (StrUtil.isBlank(batchCode) || payerUserCodes == null || payerUserCodes.isEmpty()) {
            return;
        }
        List<String> codes = payerUserCodes.stream()
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .distinct()
                .toList();
        if (codes.isEmpty()) {
            return;
        }
        DistUserBillSettleRecord update = new DistUserBillSettleRecord();
        update.setDistSettleBatchCode(batchCode);
        update.setDistSettleBatchStatusCode(StatusCodeEnum.NO.getCode());
        update.setDistSettleAppliedStatusCode(StatusCodeEnum.YES.getCode());
        update.setUpdateTime(now);
        distUserBillSettleRecordMapper.update(update, new UpdateWrapper<DistUserBillSettleRecord>().lambda()
                .in(DistUserBillSettleRecord::getDistPayerUserCode, codes)
                .eq(DistUserBillSettleRecord::getDistBizLineCode, bizLine)
                .isNull(DistUserBillSettleRecord::getDistSettleBatchCode));
    }

    private void syncSettleRecordsOnBatchComplete(String batchCode, LocalDateTime now) {
        if (StrUtil.isBlank(batchCode)) {
            return;
        }
        String settledCode = StatusCodeEnum.YES.getCode();
        DistUserBillSettleRecord update = new DistUserBillSettleRecord();
        update.setDistSettleBatchStatusCode(settledCode);
        update.setDistSettledStatusCode(settledCode);
        update.setDistSettledAt(now);
        update.setDistInServiceStatusCode(StatusCodeEnum.NO.getCode());
        update.setUpdateTime(now);
        distUserBillSettleRecordMapper.update(update, new UpdateWrapper<DistUserBillSettleRecord>().lambda()
                .eq(DistUserBillSettleRecord::getDistSettleBatchCode, batchCode));
    }

    private DistSettleBatch resolveBatch(String id, String distSettleBatchCode) {
        if (StrUtil.isNotBlank(id)) {
            return distSettleBatchMapper.selectById(id.trim());
        }
        if (StrUtil.isNotBlank(distSettleBatchCode)) {
            return distSettleBatchMapper.selectOne(new QueryWrapper<DistSettleBatch>().lambda()
                    .eq(DistSettleBatch::getDistSettleBatchCode, distSettleBatchCode.trim())
                    .last("LIMIT 1"));
        }
        throw new IllegalArgumentException("批次 id 或流水编码不能为空");
    }

    private static BigDecimal defaultAmount(BigDecimal amount) {
        return Objects.requireNonNullElse(amount, BigDecimal.ZERO);
    }

    private static DistSettleBatchDTO toDto(DistSettleBatch batch) {
        DistSettleBatchDTO dto = new DistSettleBatchDTO();
        dto.setId(batch.getId());
        dto.setCreateTime(batch.getCreateTime());
        dto.setDistSettleBatchCode(batch.getDistSettleBatchCode());
        dto.setDistBizLineCode(batch.getDistBizLineCode());
        dto.setMkCompanyCode(batch.getMkCompanyCode());
        dto.setMkCompanyName(batch.getMkCompanyName());
        dto.setMkCompanyAdminUserCode(batch.getMkCompanyAdminUserCode());
        dto.setDistMatchmakerUserCode(batch.getDistMatchmakerUserCode());
        dto.setDistApplyAt(batch.getDistApplyAt());
        dto.setDistSettledStatusCode(batch.getDistSettledStatusCode());
        dto.setDistSettleTotalAmount(batch.getDistSettleTotalAmount());
        dto.setDistSettledAt(batch.getDistSettledAt());
        return dto;
    }
}

package pub.module.verification.biz.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.verification.api.dto.PhoneTwoFactorChannelOutcome;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyRequest;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyResult;
import pub.module.verification.api.service.ApiPhoneTwoFactorVerifyService;
import pub.module.verification.biz.service.SpiPhoneTwoFactorChannel;
import pub.module.verification.biz.messaging.PhoneTwoFactorVerifiedPublisher;
import pub.module.verification.crud.entity.NpRecord;
import pub.module.verification.crud.service.NpRecordService;

import java.time.LocalDateTime;

/**
 * {@link ApiPhoneTwoFactorVerifyService} 实现：委托 {@link SpiPhoneTwoFactorChannel} 落库 {@link NpRecord}，
 * 事务提交后发布 MQ {@code verification.phone-two-factor.verified}。
 */
@Slf4j
@Service
public class ApiPhoneTwoFactorVerifyServiceImpl implements ApiPhoneTwoFactorVerifyService {

    @Resource
    private SpiPhoneTwoFactorChannel phoneTwoFactorChannel;
    @Resource
    private NpRecordService npRecordService;
    @Resource
    private PhoneTwoFactorVerifiedPublisher phoneTwoFactorVerifiedPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhoneTwoFactorVerifyResult verify(PhoneTwoFactorVerifyRequest request) {
        String phone = request.getPhone().trim();
        String realName = request.getRealName().trim();
        String sourceModuleCode = request.getNpRecordSourceModuleCode().trim();
        String bizCode = StrUtil.trim(request.getNpRecordBizCode());
        String recordCode = StrUtil.blankToDefault(request.getNpRecordCode(), IdUtil.getSnowflakeNextIdStr());

        PhoneTwoFactorChannelOutcome outcome = phoneTwoFactorChannel.verify(phone, realName);
        String passedCode = resolvePassedStatus(outcome);

        LocalDateTime now = LocalDateTime.now();
        NpRecord row = new NpRecord();
        row.setNpRecordCode(recordCode);
        row.setNpRecordSourceModuleCode(sourceModuleCode);
        row.setNpRecordBizCode(bizCode);
        row.setNpRecordPhone(phone);
        row.setNpRecordRealName(realName);
        row.setNpRecordPassedStatusCode(passedCode);
        row.setNpRecordProviderCode(outcome.getProviderCode());
        row.setNpRecordVendorRequestId(outcome.getVendorRequestId());
        row.setNpRecordVendorMessage(StrUtil.sub(StrUtil.nullToEmpty(outcome.getVendorMessage()), 0, 512));
        row.setNpRecordBasicCarrier(outcome.getBasicCarrier());
        row.setNpRecordVendorRaw(StrUtil.sub(StrUtil.nullToEmpty(outcome.getRawSummary()), 0, 65535));
        row.setCreateTime(now);
        row.setUpdateTime(now);
        row.setDeleted(0);

        npRecordService.save(row);

        PhoneTwoFactorVerifyResult result = PhoneTwoFactorVerifyResult.builder()
                .id(row.getId())
                .npRecordCode(row.getNpRecordCode())
                .npRecordSourceModuleCode(row.getNpRecordSourceModuleCode())
                .npRecordBizCode(row.getNpRecordBizCode())
                .npRecordPhone(row.getNpRecordPhone())
                .npRecordRealName(row.getNpRecordRealName())
                .npRecordPassedStatusCode(row.getNpRecordPassedStatusCode())
                .npRecordVendorBizCode(outcome.getVendorBizCode())
                .npRecordProviderCode(row.getNpRecordProviderCode())
                .npRecordVendorRequestId(row.getNpRecordVendorRequestId())
                .npRecordVendorMessage(row.getNpRecordVendorMessage())
                .npRecordBasicCarrier(row.getNpRecordBasicCarrier())
                .build();

        phoneTwoFactorVerifiedPublisher.publishAfterCommit(request, outcome, result);
        return result;
    }

    private static String resolvePassedStatus(PhoneTwoFactorChannelOutcome outcome) {
        if (!outcome.isApiReachable()) {
            return "E";
        }
        String vendorCode = StrUtil.nullToEmpty(outcome.getVendorCode());
        boolean apiOk = "OK".equalsIgnoreCase(vendorCode) || "200".equals(vendorCode);
        if (!apiOk) {
            return "E";
        }
        String bizCode = StrUtil.trim(outcome.getVendorBizCode());
        if ("1".equals(bizCode)) {
            return AliyunSpiPhoneTwoFactorChannel.PASSED_YES;
        }
        if ("2".equals(bizCode) || "3".equals(bizCode)) {
            return AliyunSpiPhoneTwoFactorChannel.PASSED_NO;
        }
        return StrUtil.blankToDefault(StrUtil.trim(outcome.getIsConsistentCode()), AliyunSpiPhoneTwoFactorChannel.PASSED_NO);
    }
}

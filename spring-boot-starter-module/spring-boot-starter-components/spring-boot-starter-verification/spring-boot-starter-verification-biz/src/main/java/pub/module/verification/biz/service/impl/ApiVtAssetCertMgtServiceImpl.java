package pub.module.verification.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.verification.api.constants.VtAssetCertProcessCodeEnum;
import pub.module.verification.api.service.ApiVtAssetCertMgtService;
import pub.module.verification.crud.entity.VtAssetCertRecord;
import pub.module.verification.crud.service.VtAssetCertRecordService;

import java.time.LocalDateTime;

/**
 * 管理端-资产认证审核
 */
@Service
public class ApiVtAssetCertMgtServiceImpl implements ApiVtAssetCertMgtService {

    @Resource
    private VtAssetCertRecordService vtAssetCertRecordService;
    @Resource
    private ApiDtCustomerService apiDtCustomerService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(String id, String auditBy) {
        VtAssetCertRecord record = requireReviewing(id);
        LocalDateTime now = LocalDateTime.now();
        record.setAssetCertProcessCode(VtAssetCertProcessCodeEnum.APPROVED);
        record.setRejectReason(null);
        record.setAuditBy(StrUtil.trim(auditBy));
        record.setAuditAt(now);
        vtAssetCertRecordService.updateById(record);

        apiDtCustomerService.applyAssetCertApproved(
                record.getCusCode(),
                record.getVehicleLicensePhoto(),
                record.getRealEstateCertificatePhoto());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(String id, String rejectReason, String auditBy) {
        VtAssetCertRecord record = requireReviewing(id);
        if (StrUtil.isBlank(rejectReason)) {
            throw new IllegalArgumentException("请填写驳回原因");
        }
        LocalDateTime now = LocalDateTime.now();
        record.setAssetCertProcessCode(VtAssetCertProcessCodeEnum.REJECTED);
        record.setRejectReason(StrUtil.trim(rejectReason));
        record.setAuditBy(StrUtil.trim(auditBy));
        record.setAuditAt(now);
        vtAssetCertRecordService.updateById(record);
    }

    private VtAssetCertRecord requireReviewing(String id) {
        if (StrUtil.isBlank(id)) {
            throw new IllegalArgumentException("记录 id 不能为空");
        }
        VtAssetCertRecord record = vtAssetCertRecordService.getById(id);
        if (record == null) {
            throw new IllegalArgumentException("资产认证记录不存在");
        }
        VtAssetCertProcessCodeEnum process = VtAssetCertProcessCodeEnum.effective(record.getAssetCertProcessCode());
        if (process != VtAssetCertProcessCodeEnum.REVIEWING) {
            throw new IllegalArgumentException("仅审核中的记录可操作");
        }
        return record;
    }
}

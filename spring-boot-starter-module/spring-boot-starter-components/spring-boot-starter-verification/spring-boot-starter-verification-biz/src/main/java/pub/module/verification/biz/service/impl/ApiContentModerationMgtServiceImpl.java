package pub.module.verification.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.verification.api.constants.CmRecordProcessCodeEnum;
import pub.module.verification.api.service.ApiContentModerationMgtService;
import pub.module.verification.biz.support.ContentModerationRecordFinishedNotifier;
import pub.module.verification.crud.entity.CmRecord;
import pub.module.verification.crud.service.CmRecordService;

import java.time.LocalDateTime;

@Service
public class ApiContentModerationMgtServiceImpl implements ApiContentModerationMgtService {

    @Resource
    private CmRecordService cmRecordService;
    @Resource
    private ContentModerationRecordFinishedNotifier contentModerationRecordFinishedNotifier;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(String id, String auditBy) {
        CmRecord record = requireAuditable(id);
        LocalDateTime now = LocalDateTime.now();
        record.setCmRecordProcessCode(CmRecordProcessCodeEnum.FINISHED);
        record.setCmRecordPassedStatusCode(StatusCodeEnum.YES.getCode());
        record.setCmRecordNotPassedReason(null);
        record.setCmRecordAuditBy(StrUtil.trim(auditBy));
        record.setCmRecordAuditAt(now);
        record.setUpdateTime(now);
        cmRecordService.updateById(record);
        contentModerationRecordFinishedNotifier.notifyFinished(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(String id, String rejectReason, String auditBy) {
        CmRecord record = requireAuditable(id);
        if (StrUtil.isBlank(rejectReason)) {
            throw new IllegalArgumentException("请填写驳回原因");
        }
        LocalDateTime now = LocalDateTime.now();
        record.setCmRecordProcessCode(CmRecordProcessCodeEnum.FINISHED);
        record.setCmRecordPassedStatusCode(StatusCodeEnum.NO.getCode());
        record.setCmRecordNotPassedReason(StrUtil.sub(rejectReason.trim(), 0, 512));
        record.setCmRecordRemark(appendRemark(record.getCmRecordRemark(), "[人工驳回] " + rejectReason.trim()));
        record.setCmRecordAuditBy(StrUtil.trim(auditBy));
        record.setCmRecordAuditAt(now);
        record.setUpdateTime(now);
        cmRecordService.updateById(record);
        contentModerationRecordFinishedNotifier.notifyFinished(record);
    }

    private CmRecord requireAuditable(String id) {
        if (StrUtil.isBlank(id)) {
            throw new IllegalArgumentException("记录 id 不能为空");
        }
        CmRecord record = cmRecordService.getById(id);
        if (record == null) {
            throw new IllegalArgumentException("内容审核记录不存在");
        }
        // 待审核(0)与审核中(1)均可人工裁决；审核中多为第三方异步未回调（如微信媒体检测），允许人工兜底结束
        CmRecordProcessCodeEnum process = CmRecordProcessCodeEnum.effective(record.getCmRecordProcessCode());
        if (process == CmRecordProcessCodeEnum.FINISHED) {
            throw new IllegalArgumentException("该记录已审核结束，无需重复审核");
        }
        return record;
    }

    private static String appendRemark(String existing, String addition) {
        if (StrUtil.isBlank(existing)) {
            return StrUtil.sub(addition, 0, 65535);
        }
        return StrUtil.sub(existing + "\n" + addition, 0, 65535);
    }
}

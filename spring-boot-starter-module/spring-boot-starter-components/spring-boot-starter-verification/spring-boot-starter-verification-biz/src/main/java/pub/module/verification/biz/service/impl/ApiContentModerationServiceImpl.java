package pub.module.verification.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.verification.api.constants.CmRecordProcessCodeEnum;
import pub.module.verification.api.dto.ContentModerationAsyncCallbackDTO;
import pub.module.verification.api.dto.ContentModerationBatchResult;
import pub.module.verification.api.dto.ContentModerationItemDTO;
import pub.module.verification.api.dto.ContentModerationItemResult;
import pub.module.verification.api.dto.ContentModerationPluginOutcome;
import pub.module.verification.api.dto.ContentModerationRequest;
import pub.module.verification.api.service.ApiContentModerationService;
import pub.module.verification.biz.service.ContentModerationPluginRegistry;
import pub.module.verification.biz.service.SpiContentModerationPlugin;
import pub.module.verification.biz.support.ContentModerationRecordFinishedNotifier;
import pub.module.verification.crud.entity.CmRecord;
import pub.module.verification.crud.service.CmRecordService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ApiContentModerationServiceImpl implements ApiContentModerationService {

    private static final int NOT_PASSED_REASON_MAX_LEN = 512;

    @Resource
    private ContentModerationPluginRegistry contentModerationPluginRegistry;
    @Resource
    private CmRecordService cmRecordService;
    @Resource
    private ContentModerationRecordFinishedNotifier contentModerationRecordFinishedNotifier;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContentModerationBatchResult moderate(ContentModerationRequest request) {
        String pluginCode = StrUtil.trim(request.getCmRecordPluginCode());
        boolean manualOnly = StrUtil.isBlank(pluginCode);
        SpiContentModerationPlugin plugin = manualOnly ? null : contentModerationPluginRegistry.require(pluginCode);

        String sourceModuleCode = StrUtil.trim(request.getCmRecordSourceModuleCode());
        String userCode = StrUtil.trim(request.getCmRecordUserCode());

        List<ContentModerationItemResult> results = new ArrayList<>();
        String blockedMessage = null;

        for (ContentModerationItemDTO item : request.getItems()) {
            String typeCode = item.getCmRecordContentTypeCode().trim();
            String content = StrUtil.trim(item.getCmRecordContent());
            if (StrUtil.isBlank(content)) {
                continue;
            }
            // 内容相同则复用既有审核结果：避免重复送审/重复落库，已通过的内容直接反馈通过
            CmRecord reusable = cmRecordService.findReusableByContent(sourceModuleCode, userCode, typeCode, content);
            if (reusable != null) {
                results.add(toItemResult(reusable));
                if (blockedMessage == null && isRejected(reusable)) {
                    blockedMessage = StrUtil.blankToDefault(reusable.getCmRecordNotPassedReason(),
                            defaultNotPassedReason(typeCode, content));
                }
                log.info("内容审核命中历史记录，复用结果 sourceModule={} userCode={} type={} cmRecordCode={} process={} passed={}",
                        sourceModuleCode, userCode, typeCode, reusable.getCmRecordCode(),
                        reusable.getCmRecordProcessCode(), reusable.getCmRecordPassedStatusCode());
                continue;
            }
            ContentModerationPluginOutcome outcome = resolveOutcome(manualOnly, plugin, request, typeCode, content, pluginCode);
            ensureNotPassedReason(outcome, typeCode, content);
            CmRecord row = buildRecord(request, typeCode, content, pluginCode, outcome);
            cmRecordService.save(row);
            results.add(toItemResult(row));

            if (blockedMessage == null && shouldBlock(outcome)) {
                blockedMessage = StrUtil.blankToDefault(row.getCmRecordNotPassedReason(),
                        defaultNotPassedReason(typeCode, content));
            }
        }

        return ContentModerationBatchResult.builder()
                .passed(blockedMessage == null)
                .blockedMessage(blockedMessage)
                .items(results)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeAsyncByTraceId(ContentModerationAsyncCallbackDTO callback) {
        String traceId = StrUtil.trim(callback.getCmRecordVendorTraceId());
        CmRecord row = cmRecordService.getByVendorTraceId(traceId);
        if (row == null) {
            log.warn("内容审核异步回调未匹配记录 trace_id={}", traceId);
            return;
        }
        // 幂等：若记录已结束（如管理员已人工兜底裁决），忽略迟到的异步回调，避免覆盖人工结论并重复通知
        if (CmRecordProcessCodeEnum.FINISHED == CmRecordProcessCodeEnum.effective(row.getCmRecordProcessCode())) {
            log.info("内容审核记录已结束，忽略异步回调 trace_id={} cmRecordCode={}", traceId, row.getCmRecordCode());
            return;
        }
        row.setCmRecordRemark(StrUtil.sub(StrUtil.nullToEmpty(callback.getCmRecordRemark()), 0, 65535));
        row.setUpdateTime(LocalDateTime.now());

        row.setCmRecordProcessCode(CmRecordProcessCodeEnum.FINISHED);
        row.setCmRecordAsyncStatusCode(StatusCodeEnum.NO.getCode());

        Integer errCode = callback.getErrCode();
        // 检测失败（如 -1008 下载错误）：内容未通过检测，结案为「未通过」，原因取微信 errmsg
        if (errCode != null && errCode != 0) {
            row.setCmRecordPassedStatusCode(StatusCodeEnum.NO.getCode());
            row.setCmRecordNotPassedReason(buildAsyncFailedReason(errCode, callback.getErrMsg()));
            cmRecordService.updateById(row);
            log.warn("微信内容安全异步检测失败判未通过 trace_id={} cmRecordCode={} errcode={} errmsg={}",
                    traceId, row.getCmRecordCode(), errCode, callback.getErrMsg());
            contentModerationRecordFinishedNotifier.notifyFinished(row);
            return;
        }

        // errcode==0：仅 suggest=pass 判通过；review/risky/缺失 一律未通过
        String suggest = StrUtil.trim(callback.getSuggest());
        boolean pass = "pass".equalsIgnoreCase(suggest);
        row.setCmRecordPassedStatusCode(pass ? StatusCodeEnum.YES.getCode() : StatusCodeEnum.NO.getCode());
        if (!pass) {
            row.setCmRecordNotPassedReason(buildAsyncNotPassedReason(
                    StrUtil.blankToDefault(suggest, "unknown"), row.getCmRecordContentTypeCode()));
        } else {
            row.setCmRecordNotPassedReason(null);
        }
        cmRecordService.updateById(row);
        contentModerationRecordFinishedNotifier.notifyFinished(row);
    }

    private static ContentModerationPluginOutcome resolveOutcome(boolean manualOnly,
                                                                 SpiContentModerationPlugin plugin,
                                                                 ContentModerationRequest request,
                                                                 String typeCode,
                                                                 String content,
                                                                 String pluginCode) {
        if (manualOnly) {
            return manualPendingOutcome(null);
        }
        ContentModerationPluginOutcome outcome = plugin.check(request, typeCode, content);
        if (!outcome.isApiReachable()) {
            return manualPendingOutcome(StrUtil.blankToDefault(outcome.getCmRecordRemark(),
                    "第三方插件调用失败，转人工审核"));
        }
        if (StrUtil.isBlank(outcome.getCmRecordPluginCode())) {
            outcome.setCmRecordPluginCode(pluginCode);
        }
        return outcome;
    }

    private static ContentModerationPluginOutcome manualPendingOutcome(String remark) {
        return ContentModerationPluginOutcome.builder()
                .apiReachable(true)
                .async(false)
                .cmRecordProcessCode(CmRecordProcessCodeEnum.PENDING.getCode())
                .cmRecordRemark(remark)
                .build();
    }

    private static void ensureNotPassedReason(ContentModerationPluginOutcome outcome,
                                              String typeCode,
                                              String content) {
        if (!StatusCodeEnum.NO.getCode().equals(outcome.getCmRecordPassedStatusCode())) {
            return;
        }
        if (StrUtil.isNotBlank(outcome.getCmRecordNotPassedReason())) {
            return;
        }
        outcome.setCmRecordNotPassedReason(defaultNotPassedReason(typeCode, content));
    }

    private static CmRecord buildRecord(ContentModerationRequest request, String typeCode, String content,
                                          String pluginCode, ContentModerationPluginOutcome outcome) {
        LocalDateTime now = LocalDateTime.now();
        CmRecord row = new CmRecord();
        row.setCmRecordSourceModuleCode(StrUtil.trim(request.getCmRecordSourceModuleCode()));
        row.setCmRecordBizCode(StrUtil.trim(request.getCmRecordBizCode()));
        row.setCmRecordUserCode(StrUtil.trim(request.getCmRecordUserCode()));
        row.setCmRecordUserName(StrUtil.trim(request.getCmRecordUserName()));
        row.setCmRecordContentTypeCode(typeCode);
        row.setCmRecordContent(content);
        row.setCmRecordPluginCode(StrUtil.trimToNull(
                StrUtil.blankToDefault(outcome.getCmRecordPluginCode(), pluginCode)));
        row.setCmRecordProcessCode(CmRecordProcessCodeEnum.parse(outcome.getCmRecordProcessCode()));
        row.setCmRecordAsyncStatusCode(outcome.isAsync()
                ? StatusCodeEnum.YES.getCode() : StatusCodeEnum.NO.getCode());
        row.setCmRecordPassedStatusCode(StrUtil.trimToNull(outcome.getCmRecordPassedStatusCode()));
        row.setCmRecordNotPassedReason(truncateReason(outcome.getCmRecordNotPassedReason()));
        row.setCmRecordVendorTraceId(outcome.getCmRecordVendorTraceId());
        row.setCmRecordRemark(StrUtil.sub(StrUtil.nullToEmpty(outcome.getCmRecordRemark()), 0, 65535));
        row.setCreateTime(now);
        row.setUpdateTime(now);
        row.setDeleted(0);
        return row;
    }

    private static ContentModerationItemResult toItemResult(CmRecord row) {
        CmRecordProcessCodeEnum process = row.getCmRecordProcessCode();
        return ContentModerationItemResult.builder()
                .id(row.getId())
                .cmRecordCode(row.getCmRecordCode())
                .cmRecordContentTypeCode(row.getCmRecordContentTypeCode())
                .cmRecordContent(row.getCmRecordContent())
                .cmRecordPluginCode(row.getCmRecordPluginCode())
                .cmRecordProcessCode(process != null ? process.getCode() : null)
                .cmRecordPassedStatusCode(row.getCmRecordPassedStatusCode())
                .cmRecordNotPassedReason(row.getCmRecordNotPassedReason())
                .cmRecordAsyncStatusCode(row.getCmRecordAsyncStatusCode())
                .cmRecordVendorTraceId(row.getCmRecordVendorTraceId())
                .cmRecordRemark(row.getCmRecordRemark())
                .build();
    }

    private static boolean isRejected(CmRecord row) {
        return CmRecordProcessCodeEnum.FINISHED == CmRecordProcessCodeEnum.effective(row.getCmRecordProcessCode())
                && StatusCodeEnum.NO.getCode().equals(row.getCmRecordPassedStatusCode());
    }

    private static boolean shouldBlock(ContentModerationPluginOutcome outcome) {
        if (!outcome.isApiReachable() || outcome.isAsync()) {
            return false;
        }
        if (!CmRecordProcessCodeEnum.FINISHED.getCode().equals(outcome.getCmRecordProcessCode())) {
            return false;
        }
        return StatusCodeEnum.NO.getCode().equals(outcome.getCmRecordPassedStatusCode());
    }

    private static String defaultNotPassedReason(String typeCode, String content) {
        String preview = StrUtil.sub(content, 0, 32);
        return "内容未通过合法校验（" + typeCode + "）：" + preview;
    }

    private static String buildAsyncNotPassedReason(String suggest, String typeCode) {
        if ("risky".equalsIgnoreCase(suggest)) {
            return "内容可能包含违规信息，未通过审核，请修改后重新提交";
        }
        if ("review".equalsIgnoreCase(suggest)) {
            return "内容需进一步核实，暂未通过，请修改后重新提交";
        }
        return truncateReason("微信" + StrUtil.blankToDefault(typeCode, "媒体") + "异步检测未通过，suggest=" + suggest);
    }

    private static String buildAsyncFailedReason(Integer errCode, String errMsg) {
        String msg = StrUtil.blankToDefault(errMsg, "检测失败");
        // -1008 媒体下载失败：给用户可理解的提示
        if (errCode != null && errCode == -1008) {
            return truncateReason("图片/视频无法被微信下载校验，请重新上传有效的图片后再提交");
        }
        return truncateReason("微信检测失败（errcode=" + errCode + "）：" + msg);
    }

    private static String truncateReason(String reason) {
        return StrUtil.sub(StrUtil.trim(reason), 0, NOT_PASSED_REASON_MAX_LEN);
    }
}

package pub.module.dating.biz.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.CusAuditProcessCodeEnum;
import pub.module.dating.api.service.dto.ProfileFieldAuditDTO;
import pub.module.dating.crud.entity.DtCustomerProfileAudit;
import pub.module.verification.api.constants.CmRecordProcessCodeEnum;
import pub.module.verification.api.dto.ContentModerationItemResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 资料字段访问与审核状态聚合（按 用户 + 字段 + 子项 维度）。
 */
public final class CustomerProfileEditAuditSupport {

    private CustomerProfileEditAuditSupport() {
    }

    public static final String[] PATCH_IGNORE_PROPERTY_NAMES = {
            "id", "createBy", "createTime", "updateBy", "updateTime",
            "deleted", "version", "seqNo", "orgCode",
            "cusUserCode", "cusCode", "cusProfileEditCode", "fieldAudits",
            "cusAddFriendRightValue", "cusRecommendRightValue", "cusMatchRightValue",
            "cusAddFriendDayLimit", "cusRecommendDayLimit", "cusMatchDayLimit",
            "cusMemberTypeCode", "cusMemberTypeName", "cusMemberExpireTime",
            "cusHandholdsNum", "cusAssignSalesTimeRangeArray",
            "cusAuditProcessCode",
            "cusAddFriendDayUsed", "cusRecommendDayUsed", "cusMatchDayUsed",
            "cusProfileCompletenessRate",
    };

    public static final CopyOptions PATCH_COPY_OPTIONS = CopyOptions.create()
            .setIgnoreNullValue(true)
            .setIgnoreProperties(PATCH_IGNORE_PROPERTY_NAMES);

    public static Object getFieldValue(Object bean, String fieldName) {
        return BeanUtil.getProperty(bean, fieldName);
    }

    public static void setFieldValue(Object bean, String fieldName, Object value) {
        BeanUtil.setProperty(bean, fieldName, value);
    }

    public static boolean fieldValueEquals(Object left, Object right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            // 视空字符串与 null 等价，避免清空字段被误判为变化
            String l = left == null ? "" : String.valueOf(left).trim();
            String r = right == null ? "" : String.valueOf(right).trim();
            return l.isEmpty() && r.isEmpty();
        }
        if (left instanceof Enum<?> leftEnum && right instanceof Enum<?> rightEnum) {
            return Objects.equals(leftEnum.name(), rightEnum.name())
                    || Objects.equals(String.valueOf(left), String.valueOf(right));
        }
        return Objects.equals(String.valueOf(left).trim(), String.valueOf(right).trim());
    }

    public static DtCustomerProfileAudit toAuditEntity(String cusUserCode, String fieldName, Integer fieldItemIndex,
                                                       String pendingValue, ContentModerationItemResult item) {
        DtCustomerProfileAudit audit = new DtCustomerProfileAudit();
        audit.setCusUserCode(cusUserCode);
        audit.setCusProfileAuditFieldName(fieldName);
        audit.setCusProfileAuditFieldItemIndex(fieldItemIndex);
        audit.setCusProfileAuditPendingValue(pendingValue);
        audit.setCmRecordCode(item.getCmRecordCode());
        audit.setCusProfileAuditProcessCode(item.getCmRecordProcessCode());
        audit.setCusProfileAuditPassedStatusCode(item.getCmRecordPassedStatusCode());
        audit.setCusProfileAuditNotPassedTip(item.getCmRecordNotPassedReason());
        return audit;
    }

    public static void applyFinishedEvent(DtCustomerProfileAudit audit, String processCode,
                                          String passedStatusCode, String notPassedTip) {
        audit.setCusProfileAuditProcessCode(processCode);
        audit.setCusProfileAuditPassedStatusCode(passedStatusCode);
        audit.setCusProfileAuditNotPassedTip(notPassedTip);
    }

    /**
     * 按字段还原待审核值：单值字段取其唯一明细的待审核值；多值字段按子项序号升序用逗号拼接。
     */
    public static Map<String, String> reconstructPendingValues(List<DtCustomerProfileAudit> audits) {
        Map<String, List<DtCustomerProfileAudit>> grouped = groupByField(audits);
        Map<String, String> result = new LinkedHashMap<>();
        grouped.forEach((field, rows) -> {
            rows.sort(Comparator.comparing(
                    DtCustomerProfileAudit::getCusProfileAuditFieldItemIndex,
                    Comparator.nullsFirst(Comparator.naturalOrder())));
            List<String> parts = new ArrayList<>();
            for (DtCustomerProfileAudit row : rows) {
                String value = StrUtil.trim(row.getCusProfileAuditPendingValue());
                if (StrUtil.isNotBlank(value)) {
                    parts.add(value);
                }
            }
            result.put(field, String.join(",", parts));
        });
        return result;
    }

    public static Map<String, ProfileFieldAuditDTO> aggregateFieldAudits(List<DtCustomerProfileAudit> audits) {
        Map<String, List<DtCustomerProfileAudit>> grouped = groupByField(audits);
        Map<String, ProfileFieldAuditDTO> result = new LinkedHashMap<>();
        grouped.forEach((field, rows) -> result.put(field, aggregateRows(rows)));
        return result;
    }

    public static CusAuditProcessCodeEnum resolveOverallProcess(List<DtCustomerProfileAudit> audits) {
        if (audits == null || audits.isEmpty()) {
            return CusAuditProcessCodeEnum.APPROVED;
        }
        Map<String, ProfileFieldAuditDTO> aggregated = aggregateFieldAudits(audits);
        boolean hasFailed = false;
        boolean hasReviewing = false;
        for (ProfileFieldAuditDTO audit : aggregated.values()) {
            if (isFailed(audit)) {
                hasFailed = true;
            } else if (isReviewing(audit)) {
                hasReviewing = true;
            }
        }
        if (hasFailed) {
            return CusAuditProcessCodeEnum.PENDING_MODIFY;
        }
        if (hasReviewing) {
            return CusAuditProcessCodeEnum.REVIEWING;
        }
        return CusAuditProcessCodeEnum.APPROVED;
    }

    public static boolean isFieldApproved(ProfileFieldAuditDTO audit) {
        return audit != null
                && CmRecordProcessCodeEnum.FINISHED.getCode().equals(audit.getAuditProcessCode())
                && StatusCodeEnum.YES.getCode().equals(audit.getAuditPassedStatusCode());
    }

    private static Map<String, List<DtCustomerProfileAudit>> groupByField(List<DtCustomerProfileAudit> audits) {
        Map<String, List<DtCustomerProfileAudit>> grouped = new LinkedHashMap<>();
        if (audits == null) {
            return grouped;
        }
        for (DtCustomerProfileAudit audit : audits) {
            grouped.computeIfAbsent(audit.getCusProfileAuditFieldName(), key -> new ArrayList<>()).add(audit);
        }
        return grouped;
    }

    private static ProfileFieldAuditDTO aggregateRows(List<DtCustomerProfileAudit> rows) {
        ProfileFieldAuditDTO merged = new ProfileFieldAuditDTO();
        String worstProcess = CmRecordProcessCodeEnum.FINISHED.getCode();
        String passed = StatusCodeEnum.YES.getCode();
        StringBuilder tips = new StringBuilder();
        for (DtCustomerProfileAudit row : rows) {
            if (isRowFailed(row)) {
                worstProcess = CmRecordProcessCodeEnum.FINISHED.getCode();
                passed = StatusCodeEnum.NO.getCode();
                appendTip(tips, row.getCusProfileAuditNotPassedTip());
            } else if (isRowReviewing(row)) {
                if (!StatusCodeEnum.NO.getCode().equals(passed)) {
                    worstProcess = row.getCusProfileAuditProcessCode();
                    passed = null;
                }
            }
        }
        merged.setAuditProcessCode(worstProcess);
        merged.setAuditPassedStatusCode(passed);
        merged.setAuditNotPassedTip(tips.isEmpty() ? null : tips.toString());
        return merged;
    }

    private static boolean isFailed(ProfileFieldAuditDTO audit) {
        return CmRecordProcessCodeEnum.FINISHED.getCode().equals(audit.getAuditProcessCode())
                && StatusCodeEnum.NO.getCode().equals(audit.getAuditPassedStatusCode());
    }

    private static boolean isReviewing(ProfileFieldAuditDTO audit) {
        if (isFailed(audit)) {
            return false;
        }
        String process = audit.getAuditProcessCode();
        return CmRecordProcessCodeEnum.PENDING.getCode().equals(process)
                || CmRecordProcessCodeEnum.REVIEWING.getCode().equals(process)
                || audit.getAuditPassedStatusCode() == null;
    }

    private static boolean isRowFailed(DtCustomerProfileAudit row) {
        return CmRecordProcessCodeEnum.FINISHED.getCode().equals(row.getCusProfileAuditProcessCode())
                && StatusCodeEnum.NO.getCode().equals(row.getCusProfileAuditPassedStatusCode());
    }

    private static boolean isRowReviewing(DtCustomerProfileAudit row) {
        if (isRowFailed(row)) {
            return false;
        }
        String process = row.getCusProfileAuditProcessCode();
        return CmRecordProcessCodeEnum.PENDING.getCode().equals(process)
                || CmRecordProcessCodeEnum.REVIEWING.getCode().equals(process)
                || row.getCusProfileAuditPassedStatusCode() == null;
    }

    private static void appendTip(StringBuilder tips, String tip) {
        if (tip == null || tip.isBlank()) {
            return;
        }
        if (!tips.isEmpty()) {
            tips.append('；');
        }
        tips.append(tip.trim());
    }
}

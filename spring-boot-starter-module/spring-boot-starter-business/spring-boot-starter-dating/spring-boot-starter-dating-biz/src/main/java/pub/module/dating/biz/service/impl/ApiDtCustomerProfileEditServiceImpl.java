package pub.module.dating.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.CusAuditProcessCodeEnum;
import pub.module.dating.api.service.ApiDtCustomerProfileEditService;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.dto.DtCustomerProfileEditDTO;
import pub.module.dating.api.service.dto.ProfileFieldAuditDTO;
import pub.module.dating.biz.support.CustomerProfileEditAuditSupport;
import pub.module.dating.biz.support.CustomerProfileEditModerationSupport;
import pub.module.dating.crud.entity.DtCustomer;
import pub.module.dating.crud.entity.DtCustomerProfileAudit;
import pub.module.dating.crud.service.DtCustomerProfileAuditService;
import pub.module.dating.crud.service.DtCustomerService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.verification.api.constants.CmRecordProcessCodeEnum;
import pub.module.verification.api.constants.ContentModerationPluginCodeEnum;
import pub.module.verification.api.dto.ContentModerationBatchResult;
import pub.module.verification.api.dto.ContentModerationItemDTO;
import pub.module.verification.api.dto.ContentModerationItemResult;
import pub.module.verification.api.dto.ContentModerationRecordFinishedDTO;
import pub.module.verification.api.dto.ContentModerationRequest;
import pub.module.verification.api.service.ApiContentModerationService;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 客户资料编辑（仅落「字段审核明细」dt_customer_profile_audit，审核通过后同步 customer）。
 * <p>不再维护整行快照编辑表：待审核值随明细保存，回显由 customer 叠加待审核值得到。</p>
 */
@Slf4j
@Service
public class ApiDtCustomerProfileEditServiceImpl implements ApiDtCustomerProfileEditService {

    @Resource
    private DtCustomerService customerService;
    @Resource
    private DtCustomerProfileAuditService profileAuditService;
    @Resource
    private ApiContentModerationService apiContentModerationService;
    @Resource
    private ApiDtCustomerService apiDtCustomerService;

    @Override
    public DtCustomerProfileEditDTO getCurrProfileEdit(String userCode) {
        Assert.notBlank(userCode, "用户未登录");
        apiDtCustomerService.initCustomerByUser(new UserDTO().setUserCode(userCode));
        DtCustomer customer = requireCustomer(userCode);
        List<DtCustomerProfileAudit> audits = profileAuditService.listByCusUserCode(userCode.trim());
        return toDto(customer, audits);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DtCustomerProfileEditDTO saveCurrProfileEdit(String userCode, DtCustomerDTO patch) {
        Assert.notBlank(userCode, "用户未登录");
        if (patch == null) {
            patch = new DtCustomerDTO();
        }
        apiDtCustomerService.initCustomerByUser(new UserDTO().setUserCode(userCode));
        DtCustomer customer = requireCustomer(userCode);
        validatePatchCusCode(patch, customer);
        normalizeCustomerPatch(patch);

        String trimmedUser = userCode.trim();

        boolean customerChanged = false;
        for (String fieldName : collectPatchFieldNames(patch)) {
            Object newValueObj = CustomerProfileEditAuditSupport.getFieldValue(patch, fieldName);
            if (CustomerProfileEditModerationSupport.requiresModeration(fieldName)) {
                String newValue = StrUtil.toStringOrNull(newValueObj);
                // 全量提交：本字段明细按本次提交重建。是否新增送审由审核模块按内容去重——
                // 内容未变更会命中历史记录复用结果，故「改一条才生成一条」，重复提交不会重复送审。
                profileAuditService.removeByCusUserCodeAndField(trimmedUser, fieldName);
                customerChanged |= moderateField(trimmedUser, customer, fieldName, newValue);
            } else {
                Object oldValue = CustomerProfileEditAuditSupport.getFieldValue(customer, fieldName);
                if (CustomerProfileEditAuditSupport.fieldValueEquals(newValueObj, oldValue)) {
                    continue;
                }
                CustomerProfileEditAuditSupport.setFieldValue(customer, fieldName, newValueObj);
                customerChanged = true;
            }
        }

        List<DtCustomerProfileAudit> allAudits = profileAuditService.listByCusUserCode(trimmedUser);
        CusAuditProcessCodeEnum process = CustomerProfileEditAuditSupport.resolveOverallProcess(allAudits);
        customer.setCusAuditProcessCode(process);
        customerService.updateById(customer);
        if (customerChanged) {
            apiDtCustomerService.afterCustomerProfileFieldsUpdated(trimmedUser);
        }
        return toDto(customer, allAudits);
    }

    /**
     * 内容审核异步/人工结束后回调（dating-biz 内部使用，不属于跨模块契约）。
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleCmRecordFinished(ContentModerationRecordFinishedDTO event) {
        if (event == null || !CustomerProfileEditModerationSupport.SOURCE_MODULE.equals(
                StrUtil.trim(event.getCmRecordSourceModuleCode()))) {
            return;
        }
        List<DtCustomerProfileAudit> matched = profileAuditService.listByCmRecordCode(event.getCmRecordCode());
        if (matched.isEmpty()) {
            return;
        }
        // 同一内容可能被复用到多个字段/子项（共用一条 cm_record），需全部回填结果
        String userCode = null;
        for (DtCustomerProfileAudit audit : matched) {
            CustomerProfileEditAuditSupport.applyFinishedEvent(
                    audit,
                    event.getCmRecordProcessCode(),
                    event.getCmRecordPassedStatusCode(),
                    event.getCmRecordNotPassedReason());
            profileAuditService.updateById(audit);
            if (userCode == null) {
                userCode = StrUtil.trim(audit.getCusUserCode());
            }
        }
        if (StrUtil.isBlank(userCode)) {
            return;
        }
        List<DtCustomerProfileAudit> audits = profileAuditService.listByCusUserCode(userCode);
        syncApprovedFieldsToCustomer(userCode, audits);
    }

    private boolean moderateField(String userCode, DtCustomer customer, String fieldName, String newValue) {
        List<ContentModerationItemDTO> items = CustomerProfileEditModerationSupport.buildModerationItems(fieldName, newValue);
        if (items.isEmpty()) {
            // 空值（如清空字段）：无需送审，直接写回 customer
            Object oldValue = CustomerProfileEditAuditSupport.getFieldValue(customer, fieldName);
            boolean changed = !CustomerProfileEditAuditSupport.fieldValueEquals(newValue, oldValue);
            CustomerProfileEditAuditSupport.setFieldValue(customer, fieldName, newValue);
            return changed;
        }
        List<DtCustomerProfileAudit> fieldAudits = new ArrayList<>();
        boolean fieldApproved = true;
        boolean fieldReviewing = false;
        boolean multi = items.size() > 1;
        for (int i = 0; i < items.size(); i++) {
            ContentModerationItemDTO itemDto = items.get(i);
            Integer itemIndex = multi ? i : null;

            ContentModerationRequest request = new ContentModerationRequest();
            request.setCmRecordPluginCode(ContentModerationPluginCodeEnum.WECHAT_MEDIA_CHECK.getCode());
            request.setCmRecordSourceModuleCode(CustomerProfileEditModerationSupport.SOURCE_MODULE);
            request.setCmRecordBizCode(CustomerProfileEditModerationSupport.buildBizCode(userCode, fieldName, itemIndex));
            request.setCmRecordUserCode(userCode);
            request.setCmRecordUserName(customer.getCusName());
            request.setWxSecCheckScene(1);
            request.setItems(List.of(itemDto));

            ContentModerationBatchResult result = apiContentModerationService.moderate(request);
            ContentModerationItemResult itemResult = result.getItems().isEmpty()
                    ? null : result.getItems().get(0);
            if (itemResult == null) {
                continue;
            }
            fieldAudits.add(CustomerProfileEditAuditSupport.toAuditEntity(
                    userCode, fieldName, itemIndex, itemDto.getCmRecordContent(), itemResult));

            if (isItemFailed(itemResult)) {
                fieldApproved = false;
            } else if (isItemReviewing(itemResult)) {
                fieldApproved = false;
                fieldReviewing = true;
            }
        }
        if (!fieldAudits.isEmpty()) {
            profileAuditService.saveBatch(fieldAudits);
        }
        if (fieldApproved) {
            Object oldValue = CustomerProfileEditAuditSupport.getFieldValue(customer, fieldName);
            boolean changed = !CustomerProfileEditAuditSupport.fieldValueEquals(newValue, oldValue);
            CustomerProfileEditAuditSupport.setFieldValue(customer, fieldName, newValue);
            return changed;
        }
        if (fieldReviewing) {
            log.info("资料字段异步审核中 userCode={} field={}", userCode, fieldName);
        }
        return false;
    }

    private void syncApprovedFieldsToCustomer(String userCode, List<DtCustomerProfileAudit> audits) {
        DtCustomer customer = requireCustomer(userCode);
        Map<String, ProfileFieldAuditDTO> aggregated = CustomerProfileEditAuditSupport.aggregateFieldAudits(audits);
        Map<String, String> pendingValues = CustomerProfileEditAuditSupport.reconstructPendingValues(audits);
        boolean changed = false;
        for (Map.Entry<String, ProfileFieldAuditDTO> entry : aggregated.entrySet()) {
            if (!CustomerProfileEditAuditSupport.isFieldApproved(entry.getValue())) {
                continue;
            }
            String field = entry.getKey();
            String value = pendingValues.get(field);
            Object current = CustomerProfileEditAuditSupport.getFieldValue(customer, field);
            if (!CustomerProfileEditAuditSupport.fieldValueEquals(value, current)) {
                CustomerProfileEditAuditSupport.setFieldValue(customer, field, value);
                changed = true;
            }
        }
        CusAuditProcessCodeEnum process = CustomerProfileEditAuditSupport.resolveOverallProcess(audits);
        customer.setCusAuditProcessCode(process);
        customerService.updateById(customer);
        if (changed) {
            apiDtCustomerService.afterCustomerProfileFieldsUpdated(userCode);
        }
    }

    private DtCustomerProfileEditDTO toDto(DtCustomer customer, List<DtCustomerProfileAudit> audits) {
        DtCustomerProfileEditDTO dto = BeanUtil.copyProperties(customer, DtCustomerProfileEditDTO.class);
        if (dto.getCusAuditProcessCode() == null) {
            dto.setCusAuditProcessCode(CusAuditProcessCodeEnum.APPROVED);
        }
        // 叠加待审核值：审核中/未通过的字段回显本人最近一次提交的内容
        Map<String, String> pendingValues = CustomerProfileEditAuditSupport.reconstructPendingValues(audits);
        for (Map.Entry<String, String> entry : pendingValues.entrySet()) {
            CustomerProfileEditAuditSupport.setFieldValue(dto, entry.getKey(), entry.getValue());
        }
        dto.setFieldAudits(CustomerProfileEditAuditSupport.aggregateFieldAudits(audits));
        return dto;
    }

    private DtCustomer requireCustomer(String userCode) {
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusUserCode, userCode.trim());
        DtCustomer customer = customerService.getOne(queryWrapper, false);
        Assert.notNull(customer, "客户不存在");
        return customer;
    }

    private static void validatePatchCusCode(DtCustomerDTO patchDto, DtCustomer entity) {
        String got = StrUtil.trim(patchDto.getCusCode());
        if (StrUtil.isBlank(got)) {
            return;
        }
        String expect = StrUtil.nullToDefault(entity.getCusCode(), "");
        if (StrUtil.isNotBlank(expect) && !StrUtil.equals(got, expect)) {
            throw new IllegalStateException("客户编码与当前登录不匹配");
        }
    }

    private static void normalizeCustomerPatch(DtCustomerDTO patchDto) {
        if (patchDto.getCusEducationCode() != null) {
            patchDto.setCusEducationCode(StrUtil.toStringOrNull(patchDto.getCusEducationCode()));
        }
        if (patchDto.getCusEducationName() != null) {
            patchDto.setCusEducationName(StrUtil.toStringOrNull(patchDto.getCusEducationName()));
        }
    }

    private static Set<String> collectPatchFieldNames(DtCustomerDTO patch) {
        Set<String> names = new LinkedHashSet<>();
        for (PropertyDescriptor descriptor : BeanUtil.getPropertyDescriptors(DtCustomerDTO.class)) {
            String name = descriptor.getName();
            if ("class".equals(name)) {
                continue;
            }
            if (Set.of(CustomerProfileEditAuditSupport.PATCH_IGNORE_PROPERTY_NAMES).contains(name)) {
                continue;
            }
            Object value = BeanUtil.getProperty(patch, name);
            if (value != null) {
                names.add(name);
            }
        }
        return names;
    }

    private static boolean isItemFailed(ContentModerationItemResult item) {
        return CmRecordProcessCodeEnum.FINISHED.getCode().equals(item.getCmRecordProcessCode())
                && StatusCodeEnum.NO.getCode().equals(item.getCmRecordPassedStatusCode());
    }

    private static boolean isItemReviewing(ContentModerationItemResult item) {
        if (isItemFailed(item)) {
            return false;
        }
        return CmRecordProcessCodeEnum.PENDING.getCode().equals(item.getCmRecordProcessCode())
                || CmRecordProcessCodeEnum.REVIEWING.getCode().equals(item.getCmRecordProcessCode())
                || item.getCmRecordPassedStatusCode() == null;
    }
}

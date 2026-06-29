package pub.module.dating.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.CusKinshipCodeEnum;
import pub.module.dating.api.constants.DatingRecommendConstants;
import pub.module.dating.api.service.ApiDtIntentionService;
import pub.module.dating.api.service.ApiDtRecommendedService;
import pub.module.dating.biz.service.DtPreferenceMutualService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.dto.DtIntentionDTO;
import pub.module.dating.api.service.ApiCustomerRedundantSyncService;
import pub.module.dating.biz.service.DtPreferenceDisplayService;
import pub.module.dating.crud.entity.DtContact;
import pub.module.dating.crud.entity.DtPreference;
import pub.module.dating.crud.entity.DtContactApply;
import pub.module.dating.crud.entity.DtCusMatchmakerRel;
import pub.module.dating.crud.entity.DtIntention;
import pub.module.dating.crud.entity.DtRecommended;
import pub.module.dating.crud.service.DtContactApplyService;
import pub.module.dating.crud.service.DtContactService;
import pub.module.dating.crud.service.DtCusMatchmakerRelService;
import pub.module.dating.crud.service.DtIntentionService;
import pub.module.dating.crud.service.DtPreferenceService;
import pub.module.dating.crud.service.DtRecommendedService;

/**
 * 客户资料变更后，同步交友模块各表冗余快照。
 */
@Slf4j
@Service
public class ApiCustomerRedundantSyncServiceImpl implements ApiCustomerRedundantSyncService {

    @Resource
    private DtContactService dtContactService;
    @Resource
    private DtContactApplyService dtContactApplyService;
    @Resource
    private DtIntentionService dtIntentionService;
    @Resource
    private DtRecommendedService dtRecommendedService;
    @Resource
    private DtCusMatchmakerRelService dtCusMatchmakerRelService;
    @Resource
    private DtPreferenceService dtPreferenceService;
    @Resource
    private DtPreferenceMutualService dtPreferenceMutualService;
    @Resource
    private ApiDtIntentionService apiDtIntentionService;
    @Resource
    private ApiDtRecommendedService apiDtRecommendedService;

    @Override
    public void syncAfterProfileUpdated(String userCode, DtCustomerDTO customer) {
        syncSnapshotFields(userCode, customer);
        if (shouldTriggerRecommendSideEffects(customer)) {
            if (StatusCodeEnum.YES.equals(customer.getCusComleteProfileStatusCode())) {
                runOptionalSync("preferenceMutual", () -> dtPreferenceMutualService.refreshOutgoingLikesMutualStatus(
                        StrUtil.trim(customer.getCusCode())));
                runOptionalSync("freeRecommend", () -> trySynFreeRecommendAfterProfileComplete(userCode, customer));
            }
        }
        log.info("已同步客户资料冗余字段 userCode={} cusCode={}", userCode, customer == null ? null : customer.getCusCode());
    }

    @Override
    public void syncSnapshotAfterProfileUpdated(String userCode, DtCustomerDTO customer) {
        syncSnapshotFields(userCode, customer);
        log.info("已同步客户资料冗余快照 userCode={} cusCode={}", userCode, customer == null ? null : customer.getCusCode());
    }

    private static boolean shouldTriggerRecommendSideEffects(DtCustomerDTO customer) {
        return customer != null && !StatusCodeEnum.YES.equals(customer.getCusTestStatusCode());
    }

    private void syncSnapshotFields(String userCode, DtCustomerDTO customer) {
        if (customer == null) {
            return;
        }
        String uid = StrUtil.firstNonBlank(StrUtil.trim(userCode), StrUtil.trim(customer.getCusUserCode()));
        String cid = StrUtil.trim(customer.getCusCode());
        if (StrUtil.isBlank(uid) && StrUtil.isBlank(cid)) {
            log.warn("跳过冗余同步：userCode 与 cusCode 均为空");
            return;
        }
        CusKinshipCodeEnum kinship = resolveKinship(customer);

        int updateCount = dtContactService.getBaseMapper().update(null, new LambdaUpdateWrapper<DtContact>()
                .eq(DtContact::getCusCode, customer.getCusCode())
                .set(DtContact::getCusKinshipCode, kinship));
        log.info("更新数{}", updateCount);
        runOptionalSync("contactApplyApplicant", () -> syncApplicantApplies(uid, customer, kinship));
        runOptionalSync("intention", () -> syncIntention(uid, kinship));
        runOptionalSync("contactPeer", () -> syncPeerContacts(cid, customer));
        runOptionalSync("contactApplyTarget", () -> syncTargetApplies(cid, customer, kinship));
        runOptionalSync("recommended", () -> syncRecommended(cid, customer));
        runOptionalSync("preference", () -> syncPreference(cid, customer));
        runOptionalSync("matchmakerRel", () -> syncMatchmakerRel(cid, customer));
    }

    private void runOptionalSync(String label, Runnable action) {
        try {
            action.run();
        } catch (Exception ex) {
            log.warn("冗余同步 {} 跳过: {}", label, ex.getMessage());
        }
    }

    private static CusKinshipCodeEnum resolveKinship(DtCustomerDTO customer) {
        return customer.getCusKinshipCode() != null ? customer.getCusKinshipCode() : CusKinshipCodeEnum.SELF;
    }


    /**
     * 联系人行内对方嘉宾快照：{@code cus_code} 对应当前客户
     */
    private void syncPeerContacts(String cusCode, DtCustomerDTO customer) {
        dtContactService.update(new LambdaUpdateWrapper<DtContact>()
                .eq(DtContact::getCusCode, cusCode)
                .set(DtContact::getCusAvatar, customer.getCusAvatar())
                .set(DtContact::getCusLifePhoto, customer.getCusLifePhoto())
                .set(DtContact::getCusName, customer.getCusName())
                .set(DtContact::getCusSexCode, customer.getCusSexCode())
                .set(DtContact::getCusAge, customer.getCusAge())
                .set(DtContact::getCusHeight, customer.getCusHeight())
                .set(DtContact::getCusWeight, customer.getCusWeight())
                .set(DtContact::getCusCityResidenceName, customer.getCusCityResidenceName())
                .set(DtContact::getCusMoment, customer.getCusMoment())
                .set(DtContact::getCusPhone, customer.getCusPhone())
                .set(DtContact::getCusUserCode, customer.getCusUserCode()));
    }

    /**
     * 我发起的申请：申请人快照 {@code app_cus_*}
     */
    private void syncApplicantApplies(String userCode, DtCustomerDTO customer, CusKinshipCodeEnum kinship) {
        dtContactApplyService.update(applicantApplyUpdate(userCode, customer, kinship));
    }

    /**
     * 别人向我发起的申请：被申请人快照 {@code cus_*}
     */
    private void syncTargetApplies(String cusCode, DtCustomerDTO customer, CusKinshipCodeEnum kinship) {
        dtContactApplyService.update(new LambdaUpdateWrapper<DtContactApply>()
                .eq(DtContactApply::getCusCode, cusCode)
                .set(DtContactApply::getCusAvatar, customer.getCusAvatar())
                .set(DtContactApply::getCusLifePhoto, customer.getCusLifePhoto())
                .set(DtContactApply::getCusName, customer.getCusName())
                .set(DtContactApply::getCusSexCode, customer.getCusSexCode())
                .set(DtContactApply::getCusAge, customer.getCusAge())
                .set(DtContactApply::getCusHeight, customer.getCusHeight())
                .set(DtContactApply::getCusWeight, customer.getCusWeight())
                .set(DtContactApply::getCusKinshipCode, kinship)
                .set(DtContactApply::getCusCityResidenceName, customer.getCusCityResidenceName())
                .set(DtContactApply::getCusMoment, customer.getCusMoment())
                .set(DtContactApply::getCusPhone, customer.getCusPhone())
                .set(DtContactApply::getCusUserCode, customer.getCusUserCode()));
    }

    private static LambdaUpdateWrapper<DtContactApply> applicantApplyUpdate(
            String userCode, DtCustomerDTO customer, CusKinshipCodeEnum kinship) {
        return new LambdaUpdateWrapper<DtContactApply>()
                .eq(DtContactApply::getUserCode, userCode)
                .set(DtContactApply::getAppCusCode, customer.getCusCode())
                .set(DtContactApply::getAppCusAvatar, customer.getCusAvatar())
                .set(DtContactApply::getAppCusName, customer.getCusName())
                .set(DtContactApply::getAppCusSexCode, customer.getCusSexCode())
                .set(DtContactApply::getAppCusAge, customer.getCusAge())
                .set(DtContactApply::getAppCusKinshipCode, kinship)
                .set(DtContactApply::getAppCusCityResidenceName, customer.getCusCityResidenceName())
                .set(DtContactApply::getAppCusMoment, customer.getCusMoment())
                .set(DtContactApply::getAppCusPhone, customer.getCusPhone());
    }

    private void syncIntention(String userCode, CusKinshipCodeEnum kinship) {
        dtIntentionService.update(new LambdaUpdateWrapper<DtIntention>()
                .eq(DtIntention::getIntentionUserCode, userCode)
                .set(DtIntention::getCusKinshipCode, kinship));
    }

    private void trySynFreeRecommendAfterProfileComplete(String userCode, DtCustomerDTO customer) {
        if (StrUtil.isBlank(userCode) || customer == null || StatusCodeEnum.YES.equals(customer.getCusTestStatusCode())) {
            return;
        }
        DtIntentionDTO intention = apiDtIntentionService.findDtIntentionIfPresent(userCode.trim());
        if (intention == null) {
            return;
        }
        int recommendCount = DatingRecommendConstants.resolveDailyRecommendCount(customer);
        apiDtRecommendedService.synFreeRecommend(intention, userCode.trim(), recommendCount);
    }

    /**
     * 偏好表冗余快照：当前客户作为发起方或目标方时同步姓名、头像、年龄、城市等。
     */
    private void syncPreference(String cusCode, DtCustomerDTO customer) {
        if (StrUtil.isBlank(cusCode) || customer == null) {
            return;
        }
        String displayName = DtPreferenceDisplayService.resolveDisplayName(customer);
        Integer age = DtPreferenceDisplayService.toIntegerAge(customer.getCusAge());
        dtPreferenceService.update(new LambdaUpdateWrapper<DtPreference>()
                .eq(DtPreference::getPreferenceCusCode, cusCode)
                .set(DtPreference::getPreferenceCusName, displayName)
                .set(DtPreference::getPreferenceCusAge, age)
                .set(DtPreference::getPreferenceCusAvatar, customer.getCusAvatar())
                .set(DtPreference::getPreferenceCusCityResidenceCode, customer.getCusCityResidenceCode())
                .set(DtPreference::getPreferenceCusCityResidenceName, customer.getCusCityResidenceName()));
        dtPreferenceService.update(new LambdaUpdateWrapper<DtPreference>()
                .eq(DtPreference::getPreferenceTargetCusCode, cusCode)
                .set(DtPreference::getPreferenceTargetCusName, displayName)
                .set(DtPreference::getPreferenceTargetCusAge, age)
                .set(DtPreference::getPreferenceTargetCusAvatar, customer.getCusAvatar())
                .set(DtPreference::getPreferenceTargetCusCityResidenceCode, customer.getCusCityResidenceCode())
                .set(DtPreference::getPreferenceTargetCusCityResidenceName, customer.getCusCityResidenceName()));
    }

    private void syncRecommended(String cusCode, DtCustomerDTO customer) {
        dtRecommendedService.update(new LambdaUpdateWrapper<DtRecommended>()
                .eq(DtRecommended::getCusCode, cusCode)
                .set(DtRecommended::getCusAvatar, customer.getCusAvatar())
                .set(DtRecommended::getCusLifePhoto, customer.getCusLifePhoto())
                .set(DtRecommended::getCusName, customer.getCusName())
                .set(DtRecommended::getCusNickName, StrUtil.trimToNull(customer.getCusNickName()))
                .set(DtRecommended::getCusIdentityAuthenticatedStatusCode, customer.getCusIdentityAuthenticatedStatusCode())
                .set(DtRecommended::getCusLsStatusCode, customer.getCusLsStatusCode())
                .set(DtRecommended::getCusHiddenStatusCode, customer.getCusHiddenStatusCode())
                .set(DtRecommended::getCusSexCode, customer.getCusSexCode())
                .set(DtRecommended::getCusAge, customer.getCusAge())
                .set(DtRecommended::getCusHeight, customer.getCusHeight())
                .set(DtRecommended::getCusWeight, customer.getCusWeight())
                .set(DtRecommended::getCusCityResidenceCode, customer.getCusCityResidenceCode())
                .set(DtRecommended::getCusCityResidenceName, customer.getCusCityResidenceName())
                .set(DtRecommended::getCusResidenceLngLat, customer.getCusResidenceLngLat())
                .set(DtRecommended::getCusHaveCarStatusCode, customer.getCusHaveCarStatusCode())
                .set(DtRecommended::getCusHaveHouseStatusCode, customer.getCusHaveHouseStatusCode())
                .set(DtRecommended::getCusOccupationalDescription, customer.getCusOccupationalDescription())
                .set(DtRecommended::getCusAnnualIncomeAmount, customer.getCusAnnualIncomeAmount())
                .set(DtRecommended::getCusPhone, customer.getCusPhone())
                .set(DtRecommended::getCusEducationCode, customer.getCusEducationCode())
                .set(DtRecommended::getCusEducationName, customer.getCusEducationName())
                .set(DtRecommended::getCusMoment, customer.getCusMoment())
                .set(DtRecommended::getCusMaritalStatusCode, customer.getCusMaritalStatusCode()));
    }

    private void syncMatchmakerRel(String cusCode, DtCustomerDTO customer) {
        dtCusMatchmakerRelService.update(new LambdaUpdateWrapper<DtCusMatchmakerRel>()
                .eq(DtCusMatchmakerRel::getCusCode, cusCode)
                .set(DtCusMatchmakerRel::getCusName, customer.getCusName())
                .set(DtCusMatchmakerRel::getCusNickName, StrUtil.trimToNull(customer.getCusNickName()))
                .set(DtCusMatchmakerRel::getCusIdentityAuthenticatedStatusCode, customer.getCusIdentityAuthenticatedStatusCode())
                .set(DtCusMatchmakerRel::getCusAvatar, customer.getCusAvatar())
                .set(DtCusMatchmakerRel::getCusSexCode, customer.getCusSexCode())
                .set(DtCusMatchmakerRel::getCusMoment, customer.getCusMoment())
                .set(DtCusMatchmakerRel::getCusHiddenStatusCode, customer.getCusHiddenStatusCode())
                .set(DtCusMatchmakerRel::getCusPhone, StrUtil.trimToNull(customer.getCusPhone())));
    }
}

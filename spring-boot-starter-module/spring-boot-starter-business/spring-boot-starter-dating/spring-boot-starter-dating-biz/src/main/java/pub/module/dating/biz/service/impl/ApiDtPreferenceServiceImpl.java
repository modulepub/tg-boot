package pub.module.dating.biz.service.impl;

import pub.module.common.enums.StatusCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.constants.DtLikeDegreeCodeEnum;
import pub.module.dating.api.service.ApiDtPreferenceService;
import pub.module.dating.biz.service.DtPreferenceDisplayService;
import pub.module.dating.biz.service.DtPreferenceMutualService;
import pub.module.dating.biz.util.CustomerProfileCompleteUtil;
import pub.module.dating.crud.entity.DtPreference;
import pub.module.dating.crud.service.DtPreferenceService;
import pub.module.system.api.constants.SysUserBadgeKeyEnum;
import pub.module.system.api.service.ApiSysUserBadgeService;

import cn.hutool.core.util.StrUtil;

/**
 * Api 偏好 Service
 *
 * @author tg
 * 2026-03-31 02:10:33
 */
@Service
public class ApiDtPreferenceServiceImpl implements ApiDtPreferenceService {

    @Resource
    private DtPreferenceService dtPreferenceService;
    @Resource
    private ApiDtCustomerService apiDtCustomerService;
    @Resource
    private DtPreferenceMutualService dtPreferenceMutualService;
    @Resource
    private ApiSysUserBadgeService apiSysUserBadgeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(String preferenceCusCode, String preferenceTargetCusCode, String preferenceLikeStatusCode) {
        DtLikeDegreeCodeEnum likeCode = DtLikeDegreeCodeEnum.fromJson(preferenceLikeStatusCode);
        if (likeCode == null) {
            return;
        }

        if (DtLikeDegreeCodeEnum.LIKE.equals(likeCode)) {
            DtCustomerDTO selfCus = apiDtCustomerService.getCusByCusCode(preferenceCusCode);
            CustomerProfileCompleteUtil.assertLikeAllowed(selfCus);
        }

        DtPreference mine = dtPreferenceService.getOne(new QueryWrapper<DtPreference>().lambda()
                .eq(DtPreference::getPreferenceTargetCusCode, preferenceTargetCusCode)
                .eq(DtPreference::getPreferenceCusCode, preferenceCusCode), false);

        DtCustomerDTO targetCus = apiDtCustomerService.getCusByCusCode(preferenceTargetCusCode);
        boolean becameLike = false;
        if (mine == null) {
            mine = new DtPreference();
            mine.setPreferenceCusCode(preferenceCusCode);
            mine.setPreferenceTargetCusCode(preferenceTargetCusCode);
            mine.setPreferenceLikeStatusCode(likeCode);
            mine.setPreferenceMutuaStatusCode(StatusCodeEnum.NO);
            DtPreferenceDisplayService.fillTargetFromCustomer(mine, targetCus);
            dtPreferenceService.save(mine);
            becameLike = DtLikeDegreeCodeEnum.LIKE.equals(likeCode);
        }
        else {
            DtLikeDegreeCodeEnum previousLike = mine.getPreferenceLikeStatusCode();
            mine.setPreferenceLikeStatusCode(likeCode);
            DtPreferenceDisplayService.fillTargetFromCustomer(mine, targetCus);
            dtPreferenceService.updateById(mine);
            becameLike = DtLikeDegreeCodeEnum.LIKE.equals(likeCode)
                    && !DtLikeDegreeCodeEnum.LIKE.equals(previousLike);
        }

        if (becameLike) {
            DtCustomerDTO selfCus = apiDtCustomerService.getCusByCusCode(preferenceCusCode);
            if (selfCus != null && StrUtil.isNotBlank(selfCus.getCusUserCode())) {
                apiSysUserBadgeService.incrementBadgeCount(
                        selfCus.getCusUserCode(),
                        SysUserBadgeKeyEnum.ME_MY_LIKE.getCode(),
                        1);
            }
            if (targetCus != null && StrUtil.isNotBlank(targetCus.getCusUserCode())) {
                apiSysUserBadgeService.incrementBadgeCount(
                        targetCus.getCusUserCode(),
                        SysUserBadgeKeyEnum.ME_LIKE_ME.getCode(),
                        1);
            }
        }

        if (!DtLikeDegreeCodeEnum.LIKE.equals(likeCode)) {
            dtPreferenceMutualService.clearMutualForPair(preferenceCusCode, preferenceTargetCusCode);
            return;
        }

        mine = dtPreferenceService.getById(mine.getId());
        if (mine == null) {
            return;
        }
        dtPreferenceMutualService.reconcileMutualLike(mine, preferenceCusCode, preferenceTargetCusCode);
    }

    @Override
    public void refreshOutgoingLikesMutualStatus(String preferenceCusCode) {
        dtPreferenceMutualService.refreshOutgoingLikesMutualStatus(preferenceCusCode);
    }
}


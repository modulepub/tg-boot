package pub.module.dating.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.DtLikeDegreeCodeEnum;
import pub.module.dating.api.service.ApiDtContactApplyService;
import pub.module.dating.crud.entity.DtPreference;
import pub.module.dating.crud.service.DtPreferenceService;

import java.util.List;

/**
 * 偏好双向喜欢核算（仅依赖偏好与联系人，避免与 {@code ApiDtCustomerService} 形成环）。
 */
@Service
public class DtPreferenceMutualService {

    @Resource
    private DtPreferenceService dtPreferenceService;
    @Resource
    private ApiDtContactApplyService apiDtContactApplyService;

    public void refreshOutgoingLikesMutualStatus(String preferenceCusCode) {
        if (preferenceCusCode == null || preferenceCusCode.isBlank()) {
            return;
        }
        List<DtPreference> outgoingLikes = dtPreferenceService.list(new QueryWrapper<DtPreference>().lambda()
                .eq(DtPreference::getPreferenceCusCode, preferenceCusCode.trim())
                .eq(DtPreference::getPreferenceLikeStatusCode, DtLikeDegreeCodeEnum.LIKE));
        if (outgoingLikes == null || outgoingLikes.isEmpty()) {
            return;
        }
        for (DtPreference mine : outgoingLikes) {
            String targetCode = mine.getPreferenceTargetCusCode();
            if (targetCode == null || targetCode.isBlank()) {
                continue;
            }
            reconcileMutualLike(mine, preferenceCusCode.trim(), targetCode.trim());
        }
    }

    public void reconcileMutualLike(DtPreference mine, String preferenceCusCode, String preferenceTargetCusCode) {
        DtPreference reverse = dtPreferenceService.getOne(new QueryWrapper<DtPreference>().lambda()
                .eq(DtPreference::getPreferenceCusCode, preferenceTargetCusCode)
                .eq(DtPreference::getPreferenceTargetCusCode, preferenceCusCode)
                .eq(DtPreference::getPreferenceLikeStatusCode, DtLikeDegreeCodeEnum.LIKE), false);

        if (reverse != null) {
            boolean newlyMutual = !StatusCodeEnum.YES.equals(mine.getPreferenceMutuaStatusCode());
            mine.setPreferenceMutuaStatusCode(StatusCodeEnum.YES);
            reverse.setPreferenceMutuaStatusCode(StatusCodeEnum.YES);
            dtPreferenceService.updateById(mine);
            dtPreferenceService.updateById(reverse);
            if (newlyMutual) {
                apiDtContactApplyService.ensureMutualLikeContacts(preferenceCusCode, preferenceTargetCusCode);
            }
        }
        else {
            mine.setPreferenceMutuaStatusCode(StatusCodeEnum.NO);
            dtPreferenceService.updateById(mine);
        }
    }

    public void clearMutualForPair(String cusA, String cusB) {
        DtPreference rowAb = dtPreferenceService.getOne(new QueryWrapper<DtPreference>().lambda()
                .eq(DtPreference::getPreferenceCusCode, cusA)
                .eq(DtPreference::getPreferenceTargetCusCode, cusB), false);
        DtPreference rowBa = dtPreferenceService.getOne(new QueryWrapper<DtPreference>().lambda()
                .eq(DtPreference::getPreferenceCusCode, cusB)
                .eq(DtPreference::getPreferenceTargetCusCode, cusA), false);
        if (rowAb != null) {
            rowAb.setPreferenceMutuaStatusCode(StatusCodeEnum.NO);
            dtPreferenceService.updateById(rowAb);
        }
        if (rowBa != null) {
            rowBa.setPreferenceMutuaStatusCode(StatusCodeEnum.NO);
            dtPreferenceService.updateById(rowBa);
        }
    }
}

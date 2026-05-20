package pub.module.dating.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.dating.api.constants.DtLikeDegreeCodeEnum;
import pub.module.dating.api.service.ApiDtPreferenceService;
import pub.module.dating.biz.service.DtPreferenceDisplayService;
import pub.module.dating.curd.entity.DtPreference;
import pub.module.dating.curd.service.DtPreferenceService;

/**
 * Api 偏好 Service
 *
 * @author tg
 * 2026-03-31 02:10:33
 */
@Service
public class ApiDtPreferenceServiceImpl implements ApiDtPreferenceService {

    private static final String MUTUAL_YES = "1";
    private static final String MUTUAL_NO = "0";

    @Resource
    private DtPreferenceService dtPreferenceService;
    @Resource
    private ApiCustomerService apiCustomerService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(String preferenceCusCode, String preferenceTargetCusCode, String preferenceLikeStatusCode) {
        DtPreference mine = dtPreferenceService.getOne(new QueryWrapper<DtPreference>().lambda()
                .eq(DtPreference::getPreferenceTargetCusCode, preferenceTargetCusCode)
                .eq(DtPreference::getPreferenceCusCode, preferenceCusCode), false);

        CustomerDTO targetCus = apiCustomerService.getCusByCusCode(preferenceTargetCusCode);
        if (mine == null) {
            mine = new DtPreference();
            mine.setPreferenceCusCode(preferenceCusCode);
            mine.setPreferenceTargetCusCode(preferenceTargetCusCode);
            mine.setPreferenceLikeStatusCode(preferenceLikeStatusCode);
            mine.setPreferenceMutuaStatusCode(MUTUAL_NO);
            DtPreferenceDisplayService.fillTargetFromCustomer(mine, targetCus);
            dtPreferenceService.save(mine);
        }
        else {
            mine.setPreferenceLikeStatusCode(preferenceLikeStatusCode);
            DtPreferenceDisplayService.fillTargetFromCustomer(mine, targetCus);
            dtPreferenceService.updateById(mine);
        }

        if (!DtLikeDegreeCodeEnum.LIKE.getCode().equals(preferenceLikeStatusCode)) {
            clearMutualForPair(preferenceCusCode, preferenceTargetCusCode);
            return;
        }

        DtPreference reverse = dtPreferenceService.getOne(new QueryWrapper<DtPreference>().lambda()
                .eq(DtPreference::getPreferenceCusCode, preferenceTargetCusCode)
                .eq(DtPreference::getPreferenceTargetCusCode, preferenceCusCode)
                .eq(DtPreference::getPreferenceLikeStatusCode, DtLikeDegreeCodeEnum.LIKE.getCode()), false);

        mine = dtPreferenceService.getById(mine.getId());
        if (mine == null) {
            return;
        }
        if (reverse != null) {
            mine.setPreferenceMutuaStatusCode(MUTUAL_YES);
            reverse.setPreferenceMutuaStatusCode(MUTUAL_YES);
            dtPreferenceService.updateById(mine);
            dtPreferenceService.updateById(reverse);
        }
        else {
            mine.setPreferenceMutuaStatusCode(MUTUAL_NO);
            dtPreferenceService.updateById(mine);
        }
    }

    private void clearMutualForPair(String cusA, String cusB) {
        DtPreference rowAb = dtPreferenceService.getOne(new QueryWrapper<DtPreference>().lambda()
                .eq(DtPreference::getPreferenceCusCode, cusA)
                .eq(DtPreference::getPreferenceTargetCusCode, cusB), false);
        DtPreference rowBa = dtPreferenceService.getOne(new QueryWrapper<DtPreference>().lambda()
                .eq(DtPreference::getPreferenceCusCode, cusB)
                .eq(DtPreference::getPreferenceTargetCusCode, cusA), false);
        if (rowAb != null) {
            rowAb.setPreferenceMutuaStatusCode(MUTUAL_NO);
            dtPreferenceService.updateById(rowAb);
        }
        if (rowBa != null) {
            rowBa.setPreferenceMutuaStatusCode(MUTUAL_NO);
            dtPreferenceService.updateById(rowBa);
        }
    }

}

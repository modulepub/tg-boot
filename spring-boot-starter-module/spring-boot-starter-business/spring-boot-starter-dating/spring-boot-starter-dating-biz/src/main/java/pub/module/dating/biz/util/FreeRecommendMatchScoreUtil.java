package pub.module.dating.biz.util;

import cn.hutool.core.util.StrUtil;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.dto.DtIntentionDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 免费推荐匹配分：仅按意向符合项计分，最低 20 分，每符合一项加平均分（满分 100）。
 */
public final class FreeRecommendMatchScoreUtil {

    private static final int MIN_SCORE = 20;
    private static final int MAX_SCORE = 100;

    /** 本科及以上视为高学历（与前端 customerEducation 编码一致） */
    private static final Set<String> HIGHER_EDUCATION_CODES = Set.of(
            "postdoc", "phd", "master", "bachelor"
    );

    private FreeRecommendMatchScoreUtil() {
    }

    public static BigDecimal calc(DtIntentionDTO intention, DtCustomerDTO customer) {
        if (customer == null) {
            return BigDecimal.valueOf(MIN_SCORE);
        }
        List<Boolean> checks = buildChecks(intention, customer);
        if (checks.isEmpty()) {
            return BigDecimal.valueOf(MIN_SCORE);
        }
        long matched = checks.stream().filter(Boolean::booleanValue).count();
        double avgPerItem = (MAX_SCORE - MIN_SCORE) / (double) checks.size();
        double score = MIN_SCORE + matched * avgPerItem;
        return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
    }

    private static List<Boolean> buildChecks(DtIntentionDTO intention, DtCustomerDTO customer) {
        List<Boolean> checks = new ArrayList<>();
        if (intention == null) {
            return checks;
        }
        if (intention.getIntentionSexCode() != null) {
            checks.add(intention.getIntentionSexCode().equals(customer.getCusSexCode()));
        }
        if (intention.getIntentionMinAge() != null || intention.getIntentionMaxAge() != null) {
            checks.add(matchesAge(intention, customer));
        }
        if (StatusCodeEnum.NO.equals(intention.getIntentionLdrStatusCode())
                && StrUtil.isNotBlank(intention.getIntentionCityCode())) {
            checks.add(StrUtil.equals(
                    StrUtil.trim(intention.getIntentionCityCode()),
                    StrUtil.trim(customer.getCusCityResidenceCode())));
        }
        if (StatusCodeEnum.YES.equals(intention.getIntentionHaveHouseCode())) {
            checks.add(StatusCodeEnum.YES.equals(customer.getCusHaveHouseStatusCode()));
        }
        if (StatusCodeEnum.YES.equals(intention.getIntentionHaveCarCode())) {
            checks.add(StatusCodeEnum.YES.equals(customer.getCusHaveCarStatusCode()));
        }
        if (StatusCodeEnum.YES.equals(intention.getIntentionDisabledStatusCode())) {
            checks.add(StatusCodeEnum.YES.equals(customer.getCusDisabledStatusCode()));
        }
        if (StatusCodeEnum.YES.equals(intention.getIntentionHigherEducationStatusCode())) {
            checks.add(isHigherEducation(customer.getCusEducationCode()));
        }
        return checks;
    }

    private static boolean matchesAge(DtIntentionDTO intention, DtCustomerDTO customer) {
        Long age = customer.getCusAge();
        if (age == null) {
            return false;
        }
        int ageVal = age.intValue();
        Integer min = intention.getIntentionMinAge();
        Integer max = intention.getIntentionMaxAge();
        if (min != null && ageVal < min) {
            return false;
        }
        if (max != null && ageVal > max) {
            return false;
        }
        return true;
    }

    private static boolean isHigherEducation(String educationCode) {
        if (StrUtil.isBlank(educationCode)) {
            return false;
        }
        return HIGHER_EDUCATION_CODES.contains(educationCode.trim().toLowerCase());
    }
}

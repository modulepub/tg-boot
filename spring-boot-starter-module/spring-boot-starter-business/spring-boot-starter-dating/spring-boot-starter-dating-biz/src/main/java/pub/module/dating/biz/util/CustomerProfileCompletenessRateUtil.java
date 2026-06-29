package pub.module.dating.biz.util;

import cn.hutool.core.util.StrUtil;
import pub.module.dating.api.service.dto.DtCustomerDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

/**
 * 用户端资料完整度（按可编辑字段非 null / 非空占比，0–100）。
 */
public final class CustomerProfileCompletenessRateUtil {

    private CustomerProfileCompletenessRateUtil() {
    }

    public static int calc(DtCustomerDTO customer) {
        if (customer == null) {
            return 0;
        }
        int total = PROFILE_FIELD_CHECKS.size();
        if (total == 0) {
            return 0;
        }
        int filled = 0;
        for (Predicate<DtCustomerDTO> check : PROFILE_FIELD_CHECKS) {
            if (check.test(customer)) {
                filled++;
            }
        }
        return (int) Math.round(filled * 100.0 / total);
    }

    private static final List<Predicate<DtCustomerDTO>> PROFILE_FIELD_CHECKS = List.of(
            c -> StrUtil.isNotBlank(c.getCusAvatar()),
            c -> StrUtil.isNotBlank(c.getCusNickName()),
            c -> c.getCusSexCode() != null,
            c -> c.getCusBirthday() != null,
            c -> StrUtil.isNotBlank(c.getCusCityResidenceCode()) || StrUtil.isNotBlank(c.getCusCityResidenceName()),
            c -> StrUtil.isNotBlank(c.getCusEducationCode()) || StrUtil.isNotBlank(c.getCusEducationName()),
            c -> StrUtil.isNotBlank(c.getCusOccupationalDescription()),
            c -> filledNumber(c.getCusAnnualIncomeAmount()),
            c -> filledPositiveLong(c.getCusHeight()),
            c -> filledPositiveLong(c.getCusWeight()),
            c -> c.getCusHaveHouseStatusCode() != null,
            c -> c.getCusHaveCarStatusCode() != null,
            c -> c.getCusDisabledStatusCode() != null,
            c -> c.getCusRemarriageStatusCode() != null,
            c -> StrUtil.isNotBlank(c.getCusMoment()),
            c -> StrUtil.isNotBlank(c.getCusLifePhoto()),
            c -> StrUtil.isNotBlank(c.getCusTeenagePhoto()),
            c -> c.getCusKinshipCode() != null
    );

    private static boolean filledNumber(BigDecimal value) {
        return value != null;
    }

    private static boolean filledPositiveLong(Long value) {
        return value != null && value > 0;
    }
}

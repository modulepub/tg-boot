package pub.module.dating.biz.util;

import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.service.dto.DtCustomerDTO;

/**
 * 客户主页资料是否已完善（{@code cusComleteProfileStatusCode === '1'}）。
 */
public final class CustomerProfileCompleteUtil {

    public static final String LIKE_BLOCKED_MESSAGE = "请先完善资料再喜欢对方吧";

    private CustomerProfileCompleteUtil() {
    }

    public static boolean isComplete(DtCustomerDTO customer) {
        return customer != null && StatusCodeEnum.YES.equals(customer.getCusComleteProfileStatusCode());
    }

    public static void assertLikeAllowed(DtCustomerDTO customer) {
        if (!isComplete(customer)) {
            throw new IllegalArgumentException(LIKE_BLOCKED_MESSAGE);
        }
    }
}

package pub.module.dating.biz.util;

import cn.hutool.core.util.StrUtil;
import pub.module.common.exception.BizException;
import pub.module.dating.api.service.dto.MemberBenefitConsumeResultDTO;
import pub.module.dating.api.constants.DatingErrorCodeEnum;

/**
 * 会员权益消费结果与婚恋业务异常转换。
 */
public final class DatingMemberBenefitConsumeUtil {

    private DatingMemberBenefitConsumeUtil() {
    }

    public static void assertConsumed(MemberBenefitConsumeResultDTO result) {
        if (result != null && result.isSuccess()) {
            return;
        }
        throw toBizException(result);
    }

    public static BizException toBizException(MemberBenefitConsumeResultDTO result) {
        if (result == null) {
            return new BizException(DatingErrorCodeEnum.E1000);
        }
        String code = StrUtil.trim(result.getErrorCode());
        for (DatingErrorCodeEnum value : DatingErrorCodeEnum.values()) {
            if (value.getCode().equals(code)) {
                return new BizException(value);
            }
        }
        return new BizException(DatingErrorCodeEnum.E1000);
    }
}

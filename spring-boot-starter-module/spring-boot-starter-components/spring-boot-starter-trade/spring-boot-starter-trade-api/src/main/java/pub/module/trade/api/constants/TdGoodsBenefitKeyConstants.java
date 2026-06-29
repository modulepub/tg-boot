package pub.module.trade.api.constants;

import pub.module.trade.api.dto.TdGoodsBenefitDTO;
import pub.module.trade.api.dto.TdGoodsMemberBenefitDeltaDTO;

import java.util.List;

/**
 * 商品权益 key 与默认权益值。
 */
public final class TdGoodsBenefitKeyConstants {

    /** 添加好友权益 key */
    public static final String ADD_FRIEND_NUM = "addFriendNum";
    /** 推荐权益 key */
    public static final String REC_NUM = "recNum";
    /** 牵线权益 key */
    public static final String MATCH_NUM = "matchNum";

    /** 未配置权益时的默认每日次数 */
    public static final long DEFAULT_BENEFIT_VALUE = 10L;

    private TdGoodsBenefitKeyConstants() {
    }

    public static long resolveBenefitValue(List<TdGoodsBenefitDTO> benefits, String benefitKey) {
        if (benefitKey == null || benefitKey.isBlank()) {
            return DEFAULT_BENEFIT_VALUE;
        }
        if (benefits == null || benefits.isEmpty()) {
            return DEFAULT_BENEFIT_VALUE;
        }
        String key = benefitKey.trim();
        for (TdGoodsBenefitDTO item : benefits) {
            if (item == null || item.getTdGdBnfKey() == null) {
                continue;
            }
            if (!key.equals(item.getTdGdBnfKey().trim())) {
                continue;
            }
            Long value = item.getTdGdBnfValue();
            if (value == null || value <= 0) {
                return DEFAULT_BENEFIT_VALUE;
            }
            return value;
        }
        return DEFAULT_BENEFIT_VALUE;
    }

    public static TdGoodsMemberBenefitDeltaDTO resolveMemberBenefitDelta(List<TdGoodsBenefitDTO> benefits) {
        return new TdGoodsMemberBenefitDeltaDTO(
                resolveBenefitValue(benefits, ADD_FRIEND_NUM),
                resolveBenefitValue(benefits, REC_NUM),
                resolveBenefitValue(benefits, MATCH_NUM)
        );
    }
}

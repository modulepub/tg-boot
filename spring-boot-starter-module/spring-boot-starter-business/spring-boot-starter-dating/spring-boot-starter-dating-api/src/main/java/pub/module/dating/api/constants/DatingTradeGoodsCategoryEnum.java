package pub.module.dating.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * 婚恋域可履约的商品品类编码（与 {@code td_goods.td_gd_cgy_code} / {@code td_goods_category} 对齐）。
 * <p>支付成功后由 dating-biz 消费消息并按本枚举路由，编译期可校验、重构可追踪引用。</p>
 */
@Getter
public enum DatingTradeGoodsCategoryEnum implements BaseEnum {

    CUS_RECOMMEND_RIGHT_VALUE("cusRecommendRightValue", "推荐次数"),
    CUS_MATCH_RIGHT_VALUE("cusMatchRightValue", "牵线次数"),
    CUS_ADD_FRIEND_RIGHT_VALUE("cusAddFriendRightValue", "加好友次数"),
    CUS_ACCELERATED_PLAN_30_DAY("cusAcceleratedPlan30Day", "真诚计划"),
    CONTRACT_QS_SUCCESS("contractQsSuccess", "牵手成功"),
    CONTRACT_MATCH_SUCCESS("contractMatchSuccess", "牵线成功合约"),
    CONTRACT_MARRY_SUCCESS("contractMarrySuccess", "结婚成功合约"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    DatingTradeGoodsCategoryEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static DatingTradeGoodsCategoryEnum fromJson(Object raw) {
        return BaseEnum.parse(raw, DatingTradeGoodsCategoryEnum.class);
    }

    public static boolean isDatingCategory(String tdGdCgyCode) {
        if (tdGdCgyCode == null || tdGdCgyCode.isBlank()) {
            return false;
        }
        for (DatingTradeGoodsCategoryEnum item : values()) {
            if (item.code.equals(tdGdCgyCode)) {
                return true;
            }
        }
        return false;
    }

    public static DatingTradeGoodsCategoryEnum require(String tdGdCgyCode) {
        return BaseEnum.of(tdGdCgyCode, DatingTradeGoodsCategoryEnum.class);
    }
}

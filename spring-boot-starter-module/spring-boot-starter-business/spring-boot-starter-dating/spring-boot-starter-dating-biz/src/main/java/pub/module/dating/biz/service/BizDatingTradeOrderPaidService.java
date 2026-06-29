package pub.module.dating.biz.service;

import cn.hutool.core.lang.Assert;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.constants.DatingTradeGoodsCategoryEnum;
import pub.module.dating.api.service.ApiDtCusMatchmakerRelService;
import pub.module.trade.api.dto.TdOrderGoodsDTO;

/**
 * 婚恋域：订单商品支付成功后的履约逻辑。
 */
@Slf4j
@Service
public class BizDatingTradeOrderPaidService {

    @Resource
    private ApiDtCustomerService apiDtCustomerService;
    @Resource
    private ApiDtCusMatchmakerRelService apiDtCusMatchmakerRelService;

    public void handlePaidOrderGoods(TdOrderGoodsDTO dto) {
        DatingTradeGoodsCategoryEnum category = DatingTradeGoodsCategoryEnum.require(dto.getTdGdCgyCode());
        switch (category) {
            case CUS_RECOMMEND_RIGHT_VALUE -> rechargeRecommendRight(dto);
            case CUS_MATCH_RIGHT_VALUE -> rechargeMatchRight(dto);
            case CUS_ADD_FRIEND_RIGHT_VALUE -> rechargeAddFriendRight(dto);
            case CUS_ACCELERATED_PLAN_30_DAY,
                 CONTRACT_QS_SUCCESS,
                 CONTRACT_MATCH_SUCCESS,
                 CONTRACT_MARRY_SUCCESS -> relateMatchmakerIfAbsent(dto);
            default -> log.warn("未处理的婚恋商品品类 tdGdCgyCode={}", dto.getTdGdCgyCode());
        }
    }

    private void rechargeRecommendRight(TdOrderGoodsDTO dto) {
        assertRechargeBasics(dto);
        apiDtCustomerService.rechargeMemberBenefits(
                dto.getTdOdGdCode(),
                dto.getTdOdCode(),
                dto.getTdOdSysUserCode(),
                null,
                dto.getTdGdValue().longValue(),
                null
        );
    }

    private void rechargeMatchRight(TdOrderGoodsDTO dto) {
        assertRechargeBasics(dto);
        apiDtCustomerService.rechargeMemberBenefits(
                dto.getTdOdGdCode(),
                dto.getTdOdCode(),
                dto.getTdOdSysUserCode(),
                null,
                null,
                dto.getTdGdValue().longValue()
        );
    }

    private void rechargeAddFriendRight(TdOrderGoodsDTO dto) {
        assertRechargeBasics(dto);
        apiDtCustomerService.rechargeMemberBenefits(
                dto.getTdOdGdCode(),
                dto.getTdOdCode(),
                dto.getTdOdSysUserCode(),
                dto.getTdGdValue().longValue(),
                null,
                null
        );
    }

    /** 签约/合约类商品支付成功后自动关注红娘；不依赖 {@code tdGdValue}（仅权益充值品类需要）。 */
    private void relateMatchmakerIfAbsent(TdOrderGoodsDTO dto) {
        Assert.notBlank(dto.getTdOdGdCode(), "订单商品明细编码不能为空");
        Assert.notBlank(dto.getTdOdSysUserCode(), "下单用户不能为空");
        Assert.notBlank(dto.getTdGdSysUserCode(), "商品供应商不能为空");
        apiDtCusMatchmakerRelService.relateCustomerWithMatchmakerIfAbsent(
                dto.getTdOdSysUserCode(),
                dto.getTdGdSysUserCode()
        );
    }

    private static void assertRechargeBasics(TdOrderGoodsDTO dto) {
        Assert.notNull(dto.getTdGdValue(), "商品价值不能为空");
        Assert.notBlank(dto.getTdOdGdCode(), "订单商品明细编码不能为空");
    }
}

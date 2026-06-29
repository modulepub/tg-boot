package pub.module.dating.biz.messaging;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.dating.api.constants.CusMemberTierConstants;
import pub.module.dating.api.constants.DatingTradeGoodsCategoryEnum;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.biz.service.BizDatingTradeOrderPaidService;
import pub.module.trade.api.dto.TdOrderGoodsDTO;
import pub.module.trade.api.messaging.TradeOrderGoodsPaidConsumer;

/**
 * 订阅交易模块订单支付成功消息，在婚恋域内履约（含 VIP 会员开通）。
 */
@Slf4j
@Component
public class DatingTradeOrderGoodsPaidHandler implements TradeOrderGoodsPaidConsumer.Dating {

    @Resource
    private BizDatingTradeOrderPaidService handler;
    @Resource
    private ApiDtCustomerService apiDtCustomerService;

    @Override
    public void onOrderGoodsPaid(TdOrderGoodsDTO dto) {
        if (dto == null) {
            return;
        }
        if (CusMemberTierConstants.isVipCategory(dto.getTdGdCgyCode())) {
            handleVipMemberBenefit(dto);
            return;
        }
        if (!DatingTradeGoodsCategoryEnum.isDatingCategory(dto.getTdGdCgyCode())) {
            return;
        }
        try {
            handler.handlePaidOrderGoods(dto);
        } catch (Exception ex) {
            log.error("婚恋履约失败 tdOdGdCode={} tdGdCgyCode={}",
                    dto.getTdOdGdCode(), dto.getTdGdCgyCode(), ex);
            throw ex;
        }
    }

    private void handleVipMemberBenefit(TdOrderGoodsDTO dto) {
        if (StrUtil.hasBlank(dto.getTdOdSysUserCode(), dto.getTdGdCode())) {
            log.warn("vip 会员履约跳过：缺少 userCode 或 tdGdCode, tdOdGdCode={}", dto.getTdOdGdCode());
            return;
        }
        try {
            apiDtCustomerService.activateMemberSubscription(
                    dto.getTdOdGdCode(),
                    dto.getTdOdCode(),
                    dto.getTdOdSysUserCode(),
                    dto.getTdGdCode(),
                    dto.getTdGdName(),
                    dto.getTdGdDayPeriod()
            );
        } catch (Exception ex) {
            log.error("vip 会员开通失败 tdOdGdCode={} tdGdCode={}",
                    dto.getTdOdGdCode(), dto.getTdGdCode(), ex);
            throw ex;
        }
    }
}

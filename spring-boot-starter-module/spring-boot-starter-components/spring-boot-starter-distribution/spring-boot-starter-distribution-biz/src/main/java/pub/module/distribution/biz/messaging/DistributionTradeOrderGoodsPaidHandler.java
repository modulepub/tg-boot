package pub.module.distribution.biz.messaging;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.distribution.biz.service.internal.DistUserBillSummaryMaintainer;
import pub.module.trade.api.dto.TdOrderGoodsDTO;
import pub.module.trade.api.messaging.TradeOrderGoodsPaidConsumer;

/**
 * 订阅交易订单支付成功，维护用户账单汇总与结算记录。
 */
@Slf4j
@Component
public class DistributionTradeOrderGoodsPaidHandler implements TradeOrderGoodsPaidConsumer.Distribution {

    @Resource
    private DistUserBillSummaryMaintainer distUserBillSummaryMaintainer;

    @Override
    public void onOrderGoodsPaid(TdOrderGoodsDTO dto) {
        if (dto == null) {
            return;
        }
        try {
            distUserBillSummaryMaintainer.onOrderGoodsPaid(dto);
        } catch (Exception ex) {
            log.error("分销订单支付处理失败 tdOdGdCode={}", dto.getTdOdGdCode(), ex);
            throw ex;
        }
    }
}

package pub.module.trade.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.trade.api.service.ApiTdOrderService;
import pub.module.trade.biz.service.BizTradeOrderService;
import pub.module.trade.crud.entity.TdOrder;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单业务 API 实现。
 *
 * @author tg
 */
@Service
@Slf4j
public class ApiTdOrderServiceImpl implements ApiTdOrderService {

    @Resource
    private BizTradeOrderService bizTradeOrderService;

    @Override
    public String createPaidOrder(String tdGdCode, BigDecimal num, String userCode, String userRealName, String userPhone) {
        Assert.notBlank(tdGdCode, "tdGdCode 不能为空");
        Assert.notBlank(userCode, "userCode 不能为空");
        BizTradeOrderService.OrderGoodsDTO orderGoods = new BizTradeOrderService.OrderGoodsDTO();
        orderGoods.setTdGdCode(tdGdCode.trim());
        orderGoods.setTdOdGdNum(num == null ? BigDecimal.ONE : num);
        TdOrder tdOrder = bizTradeOrderService.createOrder(
                List.of(orderGoods), userCode.trim(), StrUtil.trim(userRealName), StrUtil.trim(userPhone));
        bizTradeOrderService.paidOrder(tdOrder.getTdOdCode());
        log.info("createPaidOrder done tdOdCode={} tdGdCode={} userCode={}", tdOrder.getTdOdCode(), tdGdCode, userCode);
        return tdOrder.getTdOdCode();
    }
}

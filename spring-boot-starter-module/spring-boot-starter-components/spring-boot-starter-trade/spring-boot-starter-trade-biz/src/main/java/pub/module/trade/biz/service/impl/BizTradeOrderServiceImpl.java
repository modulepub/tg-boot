package pub.module.trade.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.BaseEntityFiled;
import pub.module.trade.api.dto.TdOrderGoodsDTO;
import pub.module.trade.api.service.SpiNotifyThirdPaidResultService;
import pub.module.trade.biz.service.BizTradeOrderService;
import pub.module.trade.api.constants.TdOdPaidStatusCodeEnum;
import pub.module.trade.curd.entity.TdGoods;
import pub.module.trade.curd.entity.TdOrder;
import pub.module.trade.curd.entity.TdOrderGoods;
import pub.module.trade.curd.service.ITdGoodsService;
import pub.module.trade.curd.service.ITdOrderGoodsService;
import pub.module.trade.curd.service.ITdOrderService;

import jakarta.annotation.Resource;
import pub.module.trade.api.service.SpiDistCommissionOnPaidService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 订单业务服务实现类
 * 实现订单创建、查询和支付等业务逻辑
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@Service
@Slf4j
public class BizTradeOrderServiceImpl implements BizTradeOrderService {
    @Resource
    private ITdGoodsService tradeGoodsService;
    @Resource
    private ITdOrderService tradeOrderService;
    @Resource
    private ITdOrderGoodsService tradeOrderGoodsService;
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Override
    public TdOrder createOrder(List<OrderGoodsDTO> tdOrderGoodsDtoList, String tdOdUserCode, String tdOdUserRealName, String tdOdUserPhone) {
        Assert.notEmpty(tdOrderGoodsDtoList, "tdGdCodeList not null");
        BigDecimal tdOdAmount = BigDecimal.ZERO;
        int index = 0;
        StringBuilder goodsNames = new StringBuilder();
        List<TdOrderGoods> tdOrderGoodsList = new ArrayList<>();
        for (OrderGoodsDTO tdOrderGoodsDTO : tdOrderGoodsDtoList) {
            TdOrderGoods tdOrderGoods = BeanUtil.copyProperties(tdOrderGoodsDTO, TdOrderGoods.class);
            TdGoods tdGoods = tradeGoodsService.getOne(new QueryWrapper<TdGoods>().lambda().eq(TdGoods::getTdGdCode, tdOrderGoods.getTdGdCode()), false);
            Assert.notNull(tdGoods, "tradeGoods not null");
            Assert.notNull(tdOrderGoods.getTdOdGdNum(), "tdOdGdNum not null");
            BeanUtil.copyProperties(tdGoods, tdOrderGoods, BaseEntityFiled.NAMES);
            if (index == 0) {
                goodsNames = new StringBuilder(tdGoods.getTdGdName());
            } else {
                goodsNames.append(",").append(tdOrderGoods.getTdGdName());
            }
            index++;
            tdOrderGoods.setTdOdGdAmount(tdGoods.getTdGdPrice().multiply(tdOrderGoods.getTdOdGdNum()));
            tdOrderGoods.setTdGdCgyCode(tdGoods.getTdGdCgyCode());
            tdOrderGoods.setTdOdSysUserCode(tdOdUserCode);
            tdOrderGoods.setTdOdSysUserPhone(tdOdUserPhone);
            tdOrderGoods.setTdOdSysUserRealName(tdOdUserRealName);
            tdOrderGoods.setTdOdPaidCode(TdOdPaidStatusCodeEnum.NOT_PAID.getCode());
            tdOdAmount = tdOdAmount.add(tdOrderGoods.getTdOdGdAmount());
            tdOrderGoodsList.add(tdOrderGoods);
        }
        TdOrder tdOrder = new TdOrder();
        tdOrder.setTdOdAmount(tdOdAmount);
        tdOrder.setTdOdSysUserCode(tdOdUserCode);
        tdOrder.setTdOdSysUserPhone(tdOdUserPhone);
        tdOrder.setTdOdSysUserRealName(tdOdUserRealName);
        tdOrder.setTdOdPaidStatusCode(TdOdPaidStatusCodeEnum.NOT_PAID.getCode());
        tdOrder.setTdOdRemark("支付{{goodsNames}}商品。".replace("{{goodsNames}}", goodsNames.toString()));
        tradeOrderService.save(tdOrder);
        tdOrderGoodsList.forEach(tradeOrderGoods -> {
            tradeOrderGoods.setTdOdCode(tdOrder.getTdOdCode());
        });
        tradeOrderGoodsService.saveBatch(tdOrderGoodsList);
        return tdOrder;
    }

    @Override
    public TdOrder queryOrderByCode(String tdOdCode) {
        return tradeOrderService.getOne(new QueryWrapper<TdOrder>().lambda().eq(TdOrder::getTdOdCode, tdOdCode), false);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TdOrder paidOrder(String tdOdCode) {
        TdOrder tdOrder = this.queryOrderByCode(tdOdCode);
        tdOrder.setTdOdPaidStatusCode(TdOdPaidStatusCodeEnum.PAID.getCode());
        tradeOrderService.updateById(tdOrder);
        List<TdOrderGoods> tdOrderGoodsList = tradeOrderGoodsService.list(
                new QueryWrapper<TdOrderGoods>().lambda()
                        .eq(TdOrderGoods::getTdOdCode, tdOdCode)
        );
        for (TdOrderGoods tdOrderGoods : tdOrderGoodsList) {
            tdOrderGoods.setTdOdPaidCode(TdOdPaidStatusCodeEnum.PAID.getCode());
        }
        tradeOrderGoodsService.updateBatchById(tdOrderGoodsList);
        executorService.submit(() -> {
            tdOrderGoodsList.forEach(this::notifyBiz);
        });
        return tdOrder;
    }

    public void notifyBiz(TdOrderGoods tdOrderGoods) {
        TdOrderGoodsDTO dto = BeanUtil.copyProperties(tdOrderGoods, TdOrderGoodsDTO.class);
        SpiNotifyThirdPaidResultService bizService = SpringUtil.getBean(tdOrderGoods.getTdGdCgyCode(), SpiNotifyThirdPaidResultService.class);
        bizService.notify(dto);
        notifyDistCommissionOnPaid(dto, tdOrderGoods.getTdOdGdCode());
    }

    /**
     * 可选插件：未引入 distribution-trade-plugin 时无 Bean，直接跳过。
     */
    private void notifyDistCommissionOnPaid(TdOrderGoodsDTO dto, String tdOdGdCode) {
        Map<String, SpiDistCommissionOnPaidService> listeners;
        try {
            listeners = SpringUtil.getBeansOfType(SpiDistCommissionOnPaidService.class);
        } catch (Exception ex) {
            return;
        }
        if (listeners == null || listeners.isEmpty()) {
            return;
        }
        for (SpiDistCommissionOnPaidService listener : listeners.values()) {
            try {
                listener.onOrderGoodsPaid(dto);
            } catch (Exception ex) {
                log.error("分销分佣处理失败 tdOdGdCode={}", tdOdGdCode, ex);
            }
        }
    }
}

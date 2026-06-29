package pub.module.trade.biz.service.impl;

import pub.module.common.enums.StatusCodeEnum;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.BaseEntityFiled;
import pub.module.trade.api.dto.TdOrderGoodsDTO;
import pub.module.trade.biz.messaging.TradeOrderGoodsPaidPublisher;
import pub.module.trade.biz.service.BizTradeOrderService;
import pub.module.trade.crud.entity.TdGoods;
import pub.module.trade.crud.entity.TdOrder;
import pub.module.trade.crud.entity.TdOrderGoods;
import pub.module.trade.crud.service.ITdGoodsService;
import pub.module.trade.crud.service.ITdOrderGoodsService;
import pub.module.trade.crud.service.ITdOrderService;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    @Resource
    private TradeOrderGoodsPaidPublisher tradeOrderGoodsPaidPublisher;

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
            tdOrderGoods.setTdOdPaidCode(StatusCodeEnum.NO.getCode());
            tdOdAmount = tdOdAmount.add(tdOrderGoods.getTdOdGdAmount());
            tdOrderGoodsList.add(tdOrderGoods);
        }
        TdOrder tdOrder = new TdOrder();
        tdOrder.setTdOdAmount(tdOdAmount);
        tdOrder.setTdOdSysUserCode(tdOdUserCode);
        tdOrder.setTdOdSysUserPhone(tdOdUserPhone);
        tdOrder.setTdOdSysUserRealName(tdOdUserRealName);
        tdOrder.setTdOdPaidStatusCode(StatusCodeEnum.NO.getCode());
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
        tdOrder.setTdOdPaidStatusCode(StatusCodeEnum.YES.getCode());
        tradeOrderService.updateById(tdOrder);
        List<TdOrderGoods> tdOrderGoodsList = tradeOrderGoodsService.list(
                new QueryWrapper<TdOrderGoods>().lambda()
                        .eq(TdOrderGoods::getTdOdCode, tdOdCode)
        );
        for (TdOrderGoods tdOrderGoods : tdOrderGoodsList) {
            tdOrderGoods.setTdOdPaidCode(StatusCodeEnum.YES.getCode());
        }
        tradeOrderGoodsService.updateBatchById(tdOrderGoodsList);
        tdOrderGoodsList.forEach(this::notifyBizAfterCommit);
        return tdOrder;
    }

    private void notifyBizAfterCommit(TdOrderGoods tdOrderGoods) {
        TdOrderGoodsDTO dto = BeanUtil.copyProperties(tdOrderGoods, TdOrderGoodsDTO.class);
        tradeOrderGoodsPaidPublisher.publishAfterCommit(dto);
    }
}

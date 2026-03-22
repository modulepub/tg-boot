package pub.module.trade.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import pub.module.data.api.constants.BaseEntityFiled;
import pub.module.trade.biz.service.BizTradeOrderService;
import pub.module.trade.api.constants.TdOdPaidCodeEnum;
import pub.module.trade.curd.entity.TdGoods;
import pub.module.trade.curd.entity.TdOrder;
import pub.module.trade.curd.entity.TdOrderGoods;
import pub.module.trade.curd.service.ITdGoodsService;
import pub.module.trade.curd.service.ITdOrderGoodsService;
import pub.module.trade.curd.service.ITdOrderService;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 订单业务服务实现类
 * 实现订单创建、查询和支付等业务逻辑
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Service
public class BizTradeOrderServiceImpl implements BizTradeOrderService {
    @Resource
    private ITdGoodsService tradeGoodsService;
    @Resource
    private ITdOrderService tradeOrderService;
    @Resource
    private ITdOrderGoodsService tradeOrderGoodsService;

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    @Override
    public TdOrder createOrder(List<TdOrderGoods> tdOrderGoodsList, String tdOdUserCode, String tdOdUserRealName, String tdOdUserPhone ) {
        Assert.notEmpty(tdOrderGoodsList, "tdGdCodeList not null");
        BigDecimal tdOdAmount = BigDecimal.ZERO;
        String tdCyCode = "";
        int index = 0;
        StringBuilder goodsNames = new StringBuilder();
        for(TdOrderGoods tdOrderGoods : tdOrderGoodsList){
            TdGoods tdGoods = tradeGoodsService.getOne(new QueryWrapper<TdGoods>().lambda().eq(TdGoods::getTdGdCode, tdOrderGoods.getTdGdCode()), false);
            Assert.notNull(tdGoods, "tradeGoods not null");
            Assert.notNull(tdOrderGoods.getTdOdGdNum(), "tdOdGdNum not null");
            Assert.notNull(tdGoods.getTdCyCode(), "tdCyCode not null");
            BeanUtil.copyProperties(tdGoods, tdOrderGoods, BaseEntityFiled.NAMES);
            if(index==0){
                goodsNames = new StringBuilder(tdGoods.getTdGdName());
                tdCyCode =  tdGoods.getTdCyCode();
            }else {
                goodsNames.append(",").append(tdOrderGoods.getTdGdName());
                Assert.isTrue(tdCyCode.equals(tdGoods.getTdCyCode()),"只能创建相同货币结算的订单");
            }
            index++;
            tdOrderGoods.setTdOdGdAmount(tdGoods.getTdGdPrice().multiply(tdOrderGoods.getTdOdGdNum()));
            tdOrderGoods.setTdGdCgyCode(tdGoods.getTdGdCgyCode());
            tdOrderGoods.setTdOdSysUserCode(tdOdUserCode);
            tdOrderGoods.setTdOdSysUserPhone(tdOdUserPhone);
            tdOrderGoods.setTdOdSysUserRealName(tdOdUserRealName);
            tdOrderGoods.setTdOdPaidCode(TdOdPaidCodeEnum.NOT_PAID.getCode());
            tdOdAmount = tdOdAmount.add(tdOrderGoods.getTdOdGdAmount());
        }
        TdOrder tdOrder = new TdOrder();
        tdOrder.setTdCyCode(tdCyCode);
        tdOrder.setTdOdAmount(tdOdAmount);
        tdOrder.setTdOdSysUserCode(tdOdUserCode);
        tdOrder.setTdOdSysUserPhone(tdOdUserPhone);
        tdOrder.setTdOdSysUserRealName(tdOdUserRealName);
        tdOrder.setTdOdPaidCode(TdOdPaidCodeEnum.NOT_PAID.getCode());
        tdOrder.setTdOdRemark("支付{{goodsNames}}商品。".replace("{{goodsNames}}", goodsNames.toString()));
        tradeOrderService.save(tdOrder);
        tdOrderGoodsList.forEach(tradeOrderGoods->{tradeOrderGoods.setTdOdCode(tdOrder.getTdOdCode());});
        tradeOrderGoodsService.saveBatch(tdOrderGoodsList);
        return tdOrder;
    }

    @Override
    public TdOrder queryOrderByCode(String tdOdCode) {
        return tradeOrderService.getOne(new QueryWrapper<TdOrder>().lambda().eq(TdOrder::getTdOdCode, tdOdCode), false);
    }

    @Override
    public TdOrder paidOrder(BigDecimal validateAmount, String tdOdCode) {
        TdOrder tdOrder = this.queryOrderByCode(tdOdCode);
        Assert.isTrue(validateAmount.compareTo(tdOrder.getTdOdAmount()) == 0, "校验订单金额失败！");
        tdOrder.setTdOdPaidCode(TdOdPaidCodeEnum.PAID.getCode());
        tradeOrderService.updateById(tdOrder);
        List<TdOrderGoods> tdOrderGoodsList = tradeOrderGoodsService.list(
                new QueryWrapper<TdOrderGoods>().lambda()
                        .eq(TdOrderGoods::getTdOdCode,tdOdCode)
        );
        for (TdOrderGoods tdOrderGoods : tdOrderGoodsList) {
            tdOrderGoods.setTdOdPaidCode(TdOdPaidCodeEnum.PAID.getCode());
        }
        tradeOrderGoodsService.updateBatchById(tdOrderGoodsList);

        return tdOrder;
    }
}

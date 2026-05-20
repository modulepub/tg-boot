package pub.module.plugins.distribution.trade;

import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.distribution.api.service.ApiDistCommissionService;
import pub.module.distribution.api.service.dto.DistOrderPaidNotifyDTO;
import pub.module.trade.api.dto.TdOrderGoodsDTO;
import pub.module.trade.api.service.SpiDistCommissionOnPaidService;

/**
 * 实现 trade 模块支付成功 SPI，桥接到 distribution 分佣服务。
 */
@Service
public class DistCommissionOnPaidTradePlugin implements SpiDistCommissionOnPaidService {

    @Resource
    private ApiDistCommissionService apiDistCommissionService;

    @Override
    public void onOrderGoodsPaid(TdOrderGoodsDTO tdOrderGoodsDTO) {
        if (tdOrderGoodsDTO == null) {
            return;
        }
        DistOrderPaidNotifyDTO notify = BeanUtil.copyProperties(tdOrderGoodsDTO, DistOrderPaidNotifyDTO.class);
        apiDistCommissionService.onOrderGoodsPaid(notify);
    }
}

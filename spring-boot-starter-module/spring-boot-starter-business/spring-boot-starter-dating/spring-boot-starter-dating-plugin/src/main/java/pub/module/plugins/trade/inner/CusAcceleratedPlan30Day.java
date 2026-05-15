package pub.module.plugins.trade.inner;

import cn.hutool.core.lang.Assert;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.dating.api.service.ApiDtCusMatchmakerRelService;
import pub.module.trade.api.dto.TdOrderGoodsDTO;
import pub.module.trade.api.service.SpiNotifyThirdPaidResultService;

@Service("cusAcceleratedPlan30Day")
public class CusAcceleratedPlan30Day implements SpiNotifyThirdPaidResultService {
    @Resource
    ApiDtCusMatchmakerRelService apiDtCusMatchmakerRelService;
    @Override
    public void notify(TdOrderGoodsDTO tdOrderGoodsDTO) {
        Assert.notNull(tdOrderGoodsDTO.getTdGdValue(), "商品价值不能为空");
        Assert.notBlank(tdOrderGoodsDTO.getTdOdGdCode(), "订单商品明细编码不能为空");
        Assert.notBlank(tdOrderGoodsDTO.getTdOdSysUserCode(), "下单用户不能为空");
        Assert.notBlank(tdOrderGoodsDTO.getTdGdSysUserCode(), "商品供应商不能为空");
        apiDtCusMatchmakerRelService.relateCustomerWithMatchmakerIfAbsent(
                tdOrderGoodsDTO.getTdOdSysUserCode(),
                tdOrderGoodsDTO.getTdGdSysUserCode());
    }



}

package pub.module.plugins.trade.inner;

import cn.hutool.core.lang.Assert;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.trade.api.dto.TdOrderGoodsDTO;
import pub.module.trade.api.service.SpiNotifyThirdPaidResultService;

@Service("cusRecommendRightValue")
public class CusRecommendRightValue implements SpiNotifyThirdPaidResultService {
    @Resource
    ApiCustomerService apiCustomerService;


    @Override
    public void notify(TdOrderGoodsDTO tdOrderGoodsDTO) {
        Assert.notNull(tdOrderGoodsDTO.getTdGdValue(), "商品价值不能为空");
        Assert.notBlank(tdOrderGoodsDTO.getTdOdGdCode(), "订单商品明细编码不能为空");
        apiCustomerService.rechargeMemberBenefits(
                tdOrderGoodsDTO.getTdOdGdCode(),
                tdOrderGoodsDTO.getTdOdCode(),
                tdOrderGoodsDTO.getTdOdSysUserCode(),
                null,
                tdOrderGoodsDTO.getTdGdValue().longValue(),
                null
        );
    }
}

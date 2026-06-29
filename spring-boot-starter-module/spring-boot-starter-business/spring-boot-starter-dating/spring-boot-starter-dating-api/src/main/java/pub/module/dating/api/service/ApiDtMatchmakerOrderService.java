package pub.module.dating.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pub.module.dating.api.service.dto.MkServiceOrderItemDTO;
import pub.module.dating.api.service.dto.MkServiceOrderStatsDTO;

/**
 * 红娘工作台-服务订单
 */
public interface ApiDtMatchmakerOrderService {

    /**
     * 分页查询当前红娘（商户）下的服务订单明细，并补充下单客户信息。
     *
     * @param matchmakerUserCode 当前登录红娘 system userCode
     */
    IPage<MkServiceOrderItemDTO> listServiceOrders(String matchmakerUserCode, Integer pageNo, Integer pageSize);

    /**
     * 汇总当前红娘名下已支付服务订单的金额与佣金统计。
     */
    MkServiceOrderStatsDTO getServiceOrderStats(String matchmakerUserCode);
}

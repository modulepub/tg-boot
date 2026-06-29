package pub.module.trade.api.service;

import java.math.BigDecimal;

/**
 * td_order 业务 API（由 trade-biz 实现）
 * @author tg
 * @since 2025-12-09
 * @version V1.0
 */
public interface ApiTdOrderService {

    /**
     * 后台代客下单并直接完成支付（赠送场景），与用户端主动下单一致，区别是跳过付费流程：
     * 创建订单后立即标记为已支付，并发布订单支付成功事件以触发履约（如会员开通）。
     *
     * @param tdGdCode     商品编码（如赠送会员的 {@code freevip}）
     * @param num          下单数量，{@code null} 时按 1 计
     * @param userCode     下单用户编码（受赠用户）
     * @param userRealName 用户真实姓名
     * @param userPhone    用户手机号
     * @return 订单编码 {@code tdOdCode}
     */
    String createPaidOrder(String tdGdCode, BigDecimal num, String userCode, String userRealName, String userPhone);
}

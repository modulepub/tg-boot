package pub.module.trade.crud.service;

import pub.module.trade.api.dto.TdOrderStatisticsDTO;
import pub.module.trade.crud.entity.TdOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单服务接口
 * 定义订单业务操作
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
public interface ITdOrderService extends IService<TdOrder> {

    TdOrderStatisticsDTO getStatistics();
}

package pub.module.trade.crud.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import pub.module.trade.api.dto.TdOrderStatisticsDTO;
import pub.module.trade.crud.entity.TdOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 订单Mapper接口
 * 提供订单数据访问操作
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
public interface TdOrderMapper extends BaseMapper<TdOrder> {

    TdOrderStatisticsDTO selectStatistics();
}

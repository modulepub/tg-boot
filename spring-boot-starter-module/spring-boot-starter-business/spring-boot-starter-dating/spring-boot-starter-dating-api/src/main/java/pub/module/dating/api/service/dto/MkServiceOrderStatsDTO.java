package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 红娘工作台-服务订单汇总统计
 */
@Data
@Schema(description = "红娘工作台-服务订单汇总统计")
public class MkServiceOrderStatsDTO implements Serializable {

    @Schema(description = "订单总金额（已支付）")
    private BigDecimal totalOrderAmount;

    @Schema(description = "佣金总金额（已支付订单预计佣金合计）")
    private BigDecimal totalCommissionAmount;

    @Schema(description = "已解锁佣金（服务期已结束或无服务期订单的预计佣金合计）")
    private BigDecimal unlockedCommissionAmount;
}

package pub.module.trade.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单金额统计")
public class TdOrderStatisticsDTO {

    @Schema(description = "下单总金额")
    private BigDecimal totalOrderAmount;

    @Schema(description = "已支付金额")
    private BigDecimal totalPaidAmount;

    @Schema(description = "今日下单总金额")
    private BigDecimal todayOrderAmount;

    @Schema(description = "今日已支付金额")
    private BigDecimal todayPaidAmount;
}

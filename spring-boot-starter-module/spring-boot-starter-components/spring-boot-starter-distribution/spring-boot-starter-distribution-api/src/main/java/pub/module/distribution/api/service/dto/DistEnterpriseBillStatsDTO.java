package pub.module.distribution.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "企业业绩汇总（旗下红娘账单汇总聚合）")
public class DistEnterpriseBillStatsDTO {

    @Schema(description = "旗下红娘人数")
    private Long distStaffCount;

    @Schema(description = "付费总金额（本人消费）")
    private BigDecimal distPaidTotalAmount;

    @Schema(description = "服务期内总金额")
    private BigDecimal distInServiceTotalAmount;

    @Schema(description = "子级用户付费总金额")
    private BigDecimal distSubPaidTotalAmount;

    @Schema(description = "子级服务期内总金额")
    private BigDecimal distSubInServiceTotalAmount;
}

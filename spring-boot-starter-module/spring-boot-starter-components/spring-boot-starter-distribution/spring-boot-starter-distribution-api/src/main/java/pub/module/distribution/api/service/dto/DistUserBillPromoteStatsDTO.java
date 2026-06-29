package pub.module.distribution.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "推广页顶部统计（按推广人维度）")
public class DistUserBillPromoteStatsDTO {

    @Schema(description = "直推下级人数")
    private Long distInviteeCount;

    @Schema(description = "子级用户付费总金额")
    private BigDecimal distSubPaidTotalAmount;

    @Schema(description = "子级服务期内总金额")
    private BigDecimal distSubInServiceTotalAmount;
}

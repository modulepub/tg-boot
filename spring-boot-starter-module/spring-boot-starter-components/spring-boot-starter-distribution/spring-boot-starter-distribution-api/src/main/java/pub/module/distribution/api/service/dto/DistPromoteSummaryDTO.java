package pub.module.distribution.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "推广页汇总")
public class DistPromoteSummaryDTO {

    @Schema(description = "可提现余额")
    private BigDecimal walAvailableBalance;

    @Schema(description = "服务期内待结算总额")
    private BigDecimal distPendingTotalAmount;

    @Schema(description = "邀请人数")
    private Long distInviteeCount;
}

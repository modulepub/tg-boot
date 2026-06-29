package pub.module.distribution.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "红娘名下客户未结算佣金汇总")
public class DistStaffSettleCustomerDTO {

    @Schema(description = "客户用户编码")
    private String distUserCode;

    @Schema(description = "客户昵称")
    private String distUserNickName;

    @Schema(description = "客户真实姓名")
    private String distUserRealName;

    @Schema(description = "客户总付费")
    private BigDecimal distPaidTotalAmount;

    @Schema(description = "服务期内总付费")
    private BigDecimal distInServiceTotalAmount;

    @Schema(description = "未结算佣金汇总")
    private BigDecimal distUnsettledCommissionAmount;
}

package pub.module.distribution.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "推广明细行")
public class DistPromoteInviteeRowDTO {

    @Schema(description = "被邀请人用户编码")
    private String distInviteeUserCode;

    @Schema(description = "昵称")
    private String userNickName;

    @Schema(description = "注册时间")
    private LocalDateTime distBindTime;

    @Schema(description = "可结算金额")
    private BigDecimal distSettleableAmount;

    @Schema(description = "服务期内金额")
    private BigDecimal distInServiceAmount;
}

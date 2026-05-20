package pub.module.distribution.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "提现申请")
public class WalWithdrawApplyDTO {

    @Schema(description = "提现金额")
    private BigDecimal walWithdrawAmount;

    @Schema(description = "银行编码 icbc/abc/boc/ccb")
    private String walWithdrawBankCode;

    @Schema(description = "银行卡号")
    private String walWithdrawCardNo;
}

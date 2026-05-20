package pub.module.distribution.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "提现记录")
public class WalWithdrawRecordDTO {

    private String walWithdrawCode;

    private String walWithdrawBankCode;

    private String walWithdrawCardDisplay;

    private BigDecimal walWithdrawAmount;

    private String walWithdrawStatusCode;

    private LocalDateTime createTime;

    private LocalDateTime walWithdrawArrivedTime;
}

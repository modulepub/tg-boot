package pub.module.distribution.api.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "结算批次")
public class DistSettleBatchDTO {

    @Schema(description = "主键 id")
    private String id;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "结算批次流水编码")
    private String distSettleBatchCode;

    @Schema(description = "业务线编码")
    private String distBizLineCode;

    @Schema(description = "红娘公司编码")
    private String mkCompanyCode;

    @Schema(description = "红娘公司名称（冗余）")
    private String mkCompanyName;

    @Schema(description = "申请管理员用户编码")
    private String mkCompanyAdminUserCode;

    @Schema(description = "红娘用户编码（按红娘结算时填写）")
    private String distMatchmakerUserCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "申请日期")
    private LocalDateTime distApplyAt;

    @Schema(description = "是否结算完成（StatusCode：0否 1是）")
    private String distSettledStatusCode;

    @Schema(description = "结算总金额")
    private BigDecimal distSettleTotalAmount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结算完成时间")
    private LocalDateTime distSettledAt;
}

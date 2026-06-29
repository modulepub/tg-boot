package pub.module.dating.api.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 红娘工作台-服务订单明细（含下单客户信息）
 */
@Data
@Schema(description = "红娘工作台-服务订单明细")
public class MkServiceOrderItemDTO implements Serializable {

    @Schema(description = "订单商品明细编码")
    private String tdOdGdCode;

    @Schema(description = "商品名称")
    private String tdGdName;

    @Schema(description = "下单金额")
    private BigDecimal tdOdGdAmount;

    @Schema(description = "服务期（天），null 或 0 表示无服务期（下单快照）")
    private Integer tdGdDayPeriod;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "下单时间")
    private LocalDateTime createTime;

    @Schema(description = "下单人 system userCode")
    private String tdOdSysUserCode;

    @Schema(description = "下单人客户编码（跳转嘉宾主页）")
    private String cusCode;

    @Schema(description = "下单人展示名")
    private String cusName;

    @Schema(description = "下单人手机号")
    private String cusPhone;

    @Schema(description = "支付状态")
    private String tdOdPaidCode;

    @Schema(description = "商品分佣比例（下单快照）")
    private BigDecimal tdGdCommissionRate;

    @Schema(description = "预计佣金（直推分佣，与结算口径一致）")
    private BigDecimal expectedCommissionAmount;
}

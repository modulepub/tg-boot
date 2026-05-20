package pub.module.distribution.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单支付成功通知（与 trade 解耦，由 trade 插件转换后传入）。
 */
@Data
@Schema(description = "订单商品支付成功-分佣通知")
public class DistOrderPaidNotifyDTO implements Serializable {

    @Schema(description = "订单商品明细编码")
    private String tdOdGdCode;

    @Schema(description = "订单编码")
    private String tdOdCode;

    @Schema(description = "下单用户编码")
    private String tdOdSysUserCode;

    @Schema(description = "下单商品行金额")
    private BigDecimal tdOdGdAmount;

    @Schema(description = "商品品类编码")
    private String tdGdCgyCode;

    @Schema(description = "商品服务期限文案")
    private String tdGdPeriod;
}

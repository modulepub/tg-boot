package pub.module.trade.api.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * td_order_goods
 * @author tg
 * @since 2025-12-09
 * @version V1.0
 */
@Data
@TableName("td_order_goods")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title="td_order_goods对象",description="td_order_goods对象")
public class TdOrderGoodsDTO implements Serializable {

   @Schema(description = "编码")
   private String tdGdCode;
   @Schema(description = "价格")
   private java.math.BigDecimal tdGdPrice;
   @Schema(description = "价值")
   private java.math.BigDecimal tdGdValue;
   @Schema(description = "描述")
   private String tdGdDescription;
   @Schema(description = "供应商")
   private String tdGdSysUserCode;
   @Schema(description = "供应商名称")
   private String tdGdSysUserRealName;
   @Schema(description = "供应商电话")
   private String tdGdSysUserPhone;
   @Schema(description = "启用状态")
   private String tdGdEnabledCode;
   @Schema(description = "服务期（天），null 或 0 表示无服务期")
   private Integer tdGdDayPeriod;
   @Schema(description = "合同")
   private String tdGdContractFile;
   @Schema(description = "名称")
   private String tdGdName;
   @Schema(description = "分佣比例")
   private java.math.BigDecimal tdGdCommissionRate;
   @Schema(description = "商品类目")
   private String tdGdCgyCode;
   @Schema(description = "订单商品编码")
   private String tdOdGdCode;
   @Schema(description = "下单数量")
   private java.math.BigDecimal tdOdGdNum;
   @Schema(description = "订单ID")
   private String tdOdCode;
   @Schema(description = "支付状态")
   private String tdOdPaidCode;
   @Schema(description = "下单人姓名")
   private String tdOdSysUserRealName;
   @Schema(description = "下单金额")
   private java.math.BigDecimal tdOdGdAmount;
   @Schema(description = "下单人电话")
   private String tdOdSysUserPhone;
   @Schema(description = "下单人账号")
   private String tdOdSysUserCode;

   @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   @Schema(description = "下单时间")
   private LocalDateTime createTime;
}

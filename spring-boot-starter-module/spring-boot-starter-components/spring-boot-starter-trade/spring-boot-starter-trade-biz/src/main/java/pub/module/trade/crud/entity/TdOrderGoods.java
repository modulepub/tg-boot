package pub.module.trade.crud.entity;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
 @Data
@TableName("td_order_goods")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title="td_order_goods对象",description="td_order_goods对象")
public class TdOrderGoods extends BaseEntity implements Serializable {

     @Schema(description = "订单商品明细编码")
     private java.lang.String tdOdGdCode;
	@Schema(description = "商品编码")
    private java.lang.String tdGdCode;
	@Schema(description = "商品价格")
    private java.math.BigDecimal tdGdPrice;
	@Schema(description = "商品价值")
    private java.math.BigDecimal tdGdValue;
	@Schema(description = "商品描述")
    private java.lang.String tdGdDescription;
	@Schema(description = "商品供应商")
    private java.lang.String tdGdSysUserCode;
	@Schema(description = "商品供应商名称")
    private java.lang.String tdGdSysUserRealName;
	@Schema(description = "商品供应商电话")
    private java.lang.String tdGdSysUserPhone;
	@Schema(description = "商品启用状态")
    private java.lang.String tdGdEnabledCode;
	@Schema(description = "服务期（天），null 或 0 表示无服务期")
    private Integer tdGdDayPeriod;
	@Schema(description = "商品名称")
    private java.lang.String tdGdName;
	@Schema(description = "分佣比例")
    private java.math.BigDecimal tdGdCommissionRate;
	@Schema(description = "商品类目")
    private java.lang.String tdGdCgyCode;
	@Schema(description = "下单数量")
    private java.math.BigDecimal tdOdGdNum;
	@Schema(description = "订单ID")
    private java.lang.String tdOdCode;
	@Schema(description = "支付状态")
    private java.lang.String tdOdPaidCode;
	@Schema(description = "下单人姓名")
    private java.lang.String tdOdSysUserRealName;
	@Schema(description = "下单金额")
    private java.math.BigDecimal tdOdGdAmount;
	@Schema(description = "下单人电话")
    private java.lang.String tdOdSysUserPhone;
	@Schema(description = "下单人账号")
    private java.lang.String tdOdSysUserCode;
}

package pub.module.trade.curd.entity;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.data.api.entity.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class TdOrderGoods extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

	/**编码*/
    @Schema(description = "编码")
    private java.lang.String tdGdCode;
	/**价格*/
    @Schema(description = "价格")
    private java.math.BigDecimal tdGdPrice;
	/**描述*/
    @Schema(description = "描述")
    private java.lang.String tdGdDescription;
	/**供应商*/
    @Schema(description = "供应商")
    private java.lang.String tdGdSysUserCode;
	/**供应商名称*/
    @Schema(description = "供应商名称")
    private java.lang.String tdGdSysUserRealName;
	/**供应商电话*/
    @Schema(description = "供应商电话")
    private java.lang.String tdGdSysUserPhone;
	/**启用状态*/
    @Schema(description = "启用状态")
    private java.lang.String tdGdEnabledCode;
	/**服务期限*/
    @Schema(description = "服务期限")
    private java.lang.String tdGdPeriod;
	/**货币类型*/
    @Schema(description = "货币类型")
    private java.lang.String tdCyCode;
	/**合同*/
    @Schema(description = "合同")
    private java.lang.String tdGdContractFile;
	/**名称*/
    @Schema(description = "名称")
    private java.lang.String tdGdName;
	/**商品类目*/
    @Schema(description = "商品类目")
    private java.lang.String tdGdCgyCode;
	/**订单商品编码*/
    @Schema(description = "订单商品编码")
    private java.lang.String tdOdGdCode;
	/**下单数量*/
    @Schema(description = "下单数量")
    private java.math.BigDecimal tdOdGdNum;
	/**订单ID*/
    @Schema(description = "订单ID")
    private java.lang.String tdOdCode;
	/**支付状态*/
    @Schema(description = "支付状态")
    private java.lang.String tdOdPaidCode;
	/**下单人姓名*/
    @Schema(description = "下单人姓名")
    private java.lang.String tdOdSysUserRealName;
	/**下单金额*/
    @Schema(description = "下单金额")
    private java.math.BigDecimal tdOdGdAmount;
	/**下单人电话*/
    @Schema(description = "下单人电话")
    private java.lang.String tdOdSysUserPhone;
	/**下单人账号*/
    @Schema(description = "下单人账号")
    private java.lang.String tdOdSysUserCode;
}

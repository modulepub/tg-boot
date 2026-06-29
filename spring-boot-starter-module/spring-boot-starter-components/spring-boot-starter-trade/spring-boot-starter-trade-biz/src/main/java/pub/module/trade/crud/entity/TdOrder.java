package pub.module.trade.crud.entity;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
 @Data
@TableName("td_order")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title="td_order对象",description="td_order对象")
public class TdOrder extends BaseEntity implements Serializable {

	@Schema(description = "订单编号")
    private java.lang.String tdOdCode;
	@Schema(description = "订单备注")
    private java.lang.String tdOdRemark;
	@Schema(description = "订单金额")
    private java.math.BigDecimal tdOdAmount;
	@Schema(description = "下单人账号")
    private java.lang.String tdOdSysUserCode;
	@Schema(description = "下单人电话")
    private java.lang.String tdOdSysUserPhone;
	@Schema(description = "下单人姓名")
    private java.lang.String tdOdSysUserRealName;
	@Schema(description = "支付状态")
    private java.lang.String tdOdPaidStatusCode;
     @Schema(description = "支付渠道")
     private java.lang.String tdPaidChannelCode;
	@Schema(description = "退款金额")
    private java.math.BigDecimal tdOdRefundAmount;

    @TableField(exist = false)
    @Schema(description = "下单时间检索")
    private String[] createDateRangeArray;
}

package pub.module.trade.curd.entity;

import java.io.Serial;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.data.entity.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
 /**
  * td_order
  * @author tg
  * @since 2025-12-09
  * @version V1.0
  */
@Data
@TableName("td_order")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title="td_order对象",description="td_order对象")
public class TdOrder extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


	/**订单编号*/
    @Schema(description = "订单编号")
    private java.lang.String tdOdCode;
	/**货币代码*/
    @Schema(description = "货币代码")
    private java.lang.String tdCyCode;
	/**订单备注*/
    @Schema(description = "订单备注")
    private java.lang.String tdOdRemark;
	/**订单金额*/
    @Schema(description = "订单金额")
    private java.math.BigDecimal tdOdAmount;
	/**下单人账号*/
    @Schema(description = "下单人账号")
    private java.lang.String tdOdSysUserCode;
	/**下单人电话*/
    @Schema(description = "下单人电话")
    private java.lang.String tdOdSysUserPhone;
	/**下单人姓名*/
    @Schema(description = "下单人姓名")
    private java.lang.String tdOdSysUserRealName;
	/**支付状态*/
    @Schema(description = "支付状态")
    private java.lang.String tdOdPaidCode;
	/**退款金额*/
    @Schema(description = "退款金额")
    private java.math.BigDecimal tdOdRefundAmount;
	/**商户单号*/
    @Schema(description = "商户单号")
    private java.lang.String tdOdBizCode;
}

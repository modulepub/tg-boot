package pub.module.finance.curd.entity;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;
 /**
  * 借贷分期账单还款记录
  * @author tg
  * @since 2025-10-02
  * @version V1.0
  */
@Data
@TableName("fc_loan_bill_settle_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="fc_loan_bill_settle_log对象")
public class FcLoanBillSettleLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private java.lang.String id;
	/**创建人*/
    @Schema(description = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
	/**记录编码*/
    @Schema(description = "记录编码")
    private java.lang.String fcLoanBillLogCode;
	/**还款人编码*/
    @Schema(description = "还款人编码")
    private java.lang.String userCode;
	/**借贷编码*/
    @Schema(description = "借贷编码")
    private java.lang.String fcLoanCode;
	/**账单编码*/
    @Schema(description = "账单编码")
    private java.lang.String fcLoanBillCode;
	/**结清金额*/
    @Schema(description = "结清金额")
    private java.math.BigDecimal fcLoanBillSettleAmount;
}

package pub.module.finance.curd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
 /**
  * 借贷分期账单
  * @author tg
  * @since 2025-10-02
  * @version V1.0
  */
@Data
@TableName("fc_loan_bill")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="fc_loan_bill对象")
public class FcLoanBill implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**创建人*/
    @Schema(description = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @Schema(description = "所属部门")
    private String sysOrgCode;
     /**所属部门*/
     @Schema(description = "所属用户")
     private String userCode;
	/**借贷编码*/
    @Schema(description = "借贷编码")
    private String fcLoanCode;
	/**账单编码*/
    @Schema(description = "账单编码")
    private String fcLoanBillCode;
	/**账单名称*/
    @Schema(description = "账单名称")
    private String fcLoanBillName;
	/**账单期数*/
    @Schema(description = "账单期数")
    private Integer fcLoanBillInstallmentNum;
     /**账单本金*/
     @Schema(description = "账单本金")
     private BigDecimal fcLoanBillPrincipalAmount;
	/**账单金额*/
    @Schema(description = "账单金额")
    private BigDecimal fcLoanBillAmount;
	/**账单利率（最后一期0利率，消除差值）*/
    @Schema(description = "账单利率（最后一期0利率，消除差值）")
    private String fcLoanBillInterestRate;
	/**是否逾期*/
    @Schema(description = "是否逾期")
    private String fcLoanBillOverdueStatusCode;
	/**是否结清*/
    @Schema(description = "是否结清")
    private String fcLoanBillSettleStatusCode;
     @Schema(description = "是否到期")
    private String fcLoanBillDueStatusCode;
	/**结清金额*/
    @Schema(description = "结清金额")
    private BigDecimal fcLoanBillSettleAmount;
     /**产品类型*/
     @Schema(description = "产品类型")
     private String fcProductTypeCode;
     @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
     @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
     @Schema(description = "还款时间")
     private LocalDateTime fcLoanBillRepaymentTime;
     @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
     @DateTimeFormat(pattern="yyyy-MM-dd")
     @Schema(description = "账单日期")
     private LocalDateTime fcLoanBillDate;

     @Schema(description = "距离还款日")
     @TableField(exist = false)
     private Integer fcLoanBillDateNum=1;
     /**申请金额*/
     @Schema(description = "申请金额")
     private BigDecimal fcLoanApyAmount;
     /**放款时间*/
     @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
     @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
     @Schema(description = "放款时间")
     private LocalDateTime fcLoanDisbursementTime;
}

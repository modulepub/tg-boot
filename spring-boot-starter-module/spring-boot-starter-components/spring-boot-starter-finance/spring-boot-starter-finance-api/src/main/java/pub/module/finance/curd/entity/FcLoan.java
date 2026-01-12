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
import java.time.LocalDateTime;
/**
 * 信用借贷管理
 * @author tg
 * @since 2025-10-02
 * @version V1.0
 */
@Data
@TableName("fc_loan")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="fc_loan对象")
public class FcLoan implements Serializable {
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
    /**用户编码*/
    @Schema(description = "用户编码")
    private String userCode;
    /**产品编码*/
    @Schema(description = "产品编码")
    private String fcProductCode;
    /**产品类型*/
    @Schema(description = "产品类型")
    private String fcProductTypeCode;
    @Schema(description = "信贷类型（1授信2用信）")
    private String fcLoanTypeCode;
    /**商品编码*/
    @Schema(description = "商品编码")
    private String mlGoodsCode;
    /**订单编码*/
    @Schema(description = "订单编码")
    private String mlOrderCode;
    /**借贷编码*/
    @Schema(description = "借贷编码")
    private String fcLoanCode;
    /**借贷名称*/
    @Schema(description = "借贷名称")
    private String fcLoanName;
    /**放款时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "放款时间")
    private LocalDateTime fcLoanDisbursementTime;
    /**申请时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "申请时间")
    private LocalDateTime fcLoanApplyTime;
    /**申请金额*/
    @Schema(description = "申请金额")
    private java.math.BigDecimal fcLoanApyAmount;
    /**应还金额*/
    @Schema(description = "应还金额")
    private java.math.BigDecimal fcLoanRepayableAmount;
    /**放款金额*/
    @Schema(description = "放款/授信金额")
    private java.math.BigDecimal fcLoanAmount;
    /**分期期数*/
    @Schema(description = "分期期数")
    private Integer fcLoanPeriods;
    /**已还期数*/
    @Schema(description = "已还期数")
    private Integer fcLoanSettlePeriods;
    /**利率（%）*/
    @Schema(description = "年利率（%）")
    private java.math.BigDecimal fcLoanYearInterestRate;
    @Schema(description = "审核结果（0审核中，1已通过,2已拒绝")
    private String fcLoanApprovalStatusCode;
    /**是否到账（未到账0、已到账1)*/
    @Schema(description = "是否到账（未到账0、已到账1")
    private String fcLoanReceivedStatusCode;
    /**是否逾期（1是0否，账单有逾期就算）*/
    @Schema(description = "是否逾期（1是0否，账单有逾期就算）")
    private String fcLoanOverdueStatusCode;
    /**是否结清（1是0否）*/
    @Schema(description = "是否结清（1是0否）")
    private String fcLoanSettleStatusCode;
    /**结清金额*/
    @Schema(description = "结清金额")
    private java.math.BigDecimal fcLoanSettleAmount;
    @Schema(description = "收款账户编码")
    private String fcAcCode;
    @Schema(description = "是否收款中（1是0否）")
    private String fcLoanPrpStatusCode;
    @Schema(description = "借款用途")
    private String fcLoanUseTypeCode;
    @Schema(description = "第三方借贷编码")
    private String thirdFcLoanCode;
    @Schema(description = "产品来源")
    private String fcProductSourceCode;
    @Schema(description = "第三方平台推送状态")
    private String fcLoanThirdPushStatusCode;
    @TableField(exist = false)
    @Schema(description = "产品")
    private FcProduct fcProduct;
}

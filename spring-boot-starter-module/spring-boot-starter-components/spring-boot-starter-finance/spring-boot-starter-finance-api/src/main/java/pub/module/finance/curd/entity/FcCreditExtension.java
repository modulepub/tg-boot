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
 /**
  * 授信表
  * @author tg
  * @since 2025-11-02
  * @version V1.0
  */
@Data
@TableName("fc_credit_extension")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title="fc_credit_extension对象",description="fc_credit_extension对象")
public class FcCreditExtension implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "id")
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
	/**授信编码*/
    @Schema(description = "授信编码")
    private String fcCdExCode;
	/**授信名称*/
    @Schema(description = "授信名称")
    private String fcCdExName;
	/**授信时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "授信时间")
    private java.util.Date fcCdExApprovalTime;
	/**申请时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "申请时间")
    private java.util.Date fcCdExApplyTime;
	/**授信金额（元）*/
    @Schema(description = "授信金额（元）")
    private java.math.BigDecimal fcCdExAmount;
	/**年利率（%）*/
    @Schema(description = "年利率（%）")
    private java.math.BigDecimal fcCdExYearInterestRate;
	/**审核结果*/
    @Schema(description = "审核结果")
    private String fcCdExApprovalStatusCode;
	/**第三方信贷编码*/
    @Schema(description = "第三方信贷编码")
    private String fcCdExThirdCode;
	/**是否推送第三方*/
    @Schema(description = "是否推送第三方")
    private String fcCdExThirdPushStatusCode;
	/**产品编码*/
    @Schema(description = "产品编码")
    private String fcProductCode;
	/**产品类型*/
    @Schema(description = "产品类型")
    private String fcProductTypeCode;
	/**产品来源*/
    @Schema(description = "产品来源")
    private String fcProductSourceCode;
     @TableField(exist = false)
     @Schema(description = "产品")
     private FcProduct fcProduct;
}

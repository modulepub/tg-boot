package pub.module.dating.api.service.dto;

import java.io.Serializable;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

 /**
  * 相亲意向
  * @author tg
  * @since 2025-06-15
  * @version V1.0
  */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class DtIntentionDTO implements Serializable {


	/**id*/
    //@Schema(description = "id")
    private java.lang.String id;
	/**创建人*/
    //@Schema(description = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    //@Schema(description = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    //@Schema(description = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    //@Schema(description = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    //@Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
	/**编码*/
    //@Schema(description = "编码")
    private java.lang.String dtIntentionCode;
	/**名称*/
    //@Schema(description = "名称")
    private java.lang.String dtIntentionName;
	/**性别*/
    //@Schema(description = "性别")
    private java.lang.String dtIntentionSexCode;
	/**最大年龄*/
    //@Schema(description = "最大年龄")
    private java.lang.String dtIntentionMaxAge;
	/**最小年龄*/
    //@Schema(description = "最小年龄")
    private java.lang.String dtIntentionMinAge;
	/**是否有房*/
    //@Schema(description = "是否有房")
    private java.lang.String dtIntentionHaveHouseCode;
	/**是否有车*/
    //@Schema(description = "是否有车")
    private java.lang.String dtIntentionHaveCarCode;
	/**最低年收入（元）*/
    //@Schema(description = "最低年收入（元）")
    private java.lang.String dtIntentionMinAnnualIncome;
	/**最低学历*/
    //@Schema(description = "最低学历")
    private java.lang.String dtIntentionMinDegreeCode;
	/**用户*/
    //@Schema(description = "用户")
    private java.lang.String dtIntentionSysUserCode;
	/**国家*/
    //@Schema(description = "国家")
    private java.lang.String dtIntentionCountryCode;
	/**城市*/
    //@Schema(description = "城市")
    private java.lang.String dtIntentionCityCode;
	/**服务开始状态*/
    //@Schema(description = "服务开始状态")
    private java.lang.String dtIntentionStartedCode;
	/**服务是否完成*/
    //@Schema(description = "服务是否完成")
    private java.lang.String dtIntentionSrvCompletedCode;
	/**队列位置*/
    //@Schema(description = "队列位置")
    private java.lang.Integer dtIntentionQueueLocation;
	/**匹配目标数量*/
    //@Schema(description = "匹配目标数量")
    private java.lang.Integer dtIntentionMatchesTargetNum;
	/**匹配完成数量*/
    //@Schema(description = "匹配完成数量")
    private java.lang.Integer dtIntentionMatchedNum;
	/**任务开始时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    //@Schema(description = "任务开始时间")
    private java.util.Date dtIntentionJobStartTime;
	/**任务下次执行时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    //@Schema(description = "任务下次执行时间")
    private java.util.Date dtIntentionJobNextRunTime;
	/**匹配规则*/
    //@Schema(description = "匹配规则")
    private java.lang.String dtIntentionMatchingRuleCode;
}

package pub.module.dating.curd.entity;

    import java.util.Date;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 交友意向 对象
 * @author tg
 * 2026-01-07 23:30:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "交友意向")
public class DtIntention extends BaseEntity {
                    /** 所属部门 */
                        @Schema(description = "所属部门")
                private String sysOrgCode;

                    /** 编码 */
                        @Schema(description = "编码")
                private String intentionCode;

                    /** 名称 */
                        @Schema(description = "名称")
                private String intentionName;

                    /** 最大年龄 */
                        @Schema(description = "最大年龄")
                private String intentionMaxAge;

                    /** 最小年龄 */
                        @Schema(description = "最小年龄")
                private String intentionMinAge;

                    /** 是否有房 */
                        @Schema(description = "是否有房")
                private String intentionHaveHouseCode;

                    /** 是否有车 */
                        @Schema(description = "是否有车")
                private String intentionHaveCarCode;

                    /** 最低年收入（元） */
                        @Schema(description = "最低年收入")
                private String intentionMinAnnualIncome;

                    /** 最低学历 */
                        @Schema(description = "最低学历")
                private String intentionMinDegreeCode;

                    /** 用户 */
                        @Schema(description = "用户")
                private String intentionSysUserCode;

                    /** 国家 */
                        @Schema(description = "国家")
                private String intentionCountryCode;

                    /** 城市 */
                        @Schema(description = "城市")
                private String intentionCityCode;

                    /** 服务开始状态 */
                        @Schema(description = "服务开始状态")
                private String intentionStartedCode;

                    /** 队列位置 */
                        @Schema(description = "队列位置")
                private Long intentionQueueLocation;

                    /** 匹配目标数量 */
                        @Schema(description = "匹配目标数量")
                private Long intentionMatchesTargetNum;

                    /** 匹配完成数量 */
                        @Schema(description = "匹配完成数量")
                private Long intentionMatchedNum;

                    /** 任务开始时间 */
                        @JsonFormat(pattern = "yyyy-MM-dd")
                        @Schema(description = "任务开始时间")
                private Date intentionJobStartTime;

                    /** 任务下次执行时间 */
                        @JsonFormat(pattern = "yyyy-MM-dd")
                        @Schema(description = "任务下次执行时间")
                private Date intentionJobNextRunTime;

                    /** 服务是否完成 */
                        @Schema(description = "服务是否完成")
                private String intentionSrvCompletedCode;

                    /** 匹配规则 */
                        @Schema(description = "匹配规则")
                private String intentionMatchingRuleCode;

                    /** 性别 */
                        @Schema(description = "性别")
                private String intentionSexCode;


        }

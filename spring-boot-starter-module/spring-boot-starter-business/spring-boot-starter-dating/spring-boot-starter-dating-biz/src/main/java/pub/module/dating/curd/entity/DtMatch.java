package pub.module.dating.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.api.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 匹配申请（牵线） 对象
 * @author tg
 * 2026-01-07 23:30:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "匹配申请（牵线）")
public class DtMatch extends BaseEntity {
                    /** 所属部门 */
                        @Schema(description = "所属部门")
                private String sysOrgCode;

                    /** 匹配申请编码 */
                        @Schema(description = "匹配申请编码")
                private String mtCode;

                    /** 匹配申请名称 */
                        @Schema(description = "匹配申请名称")
                private String mtName;

                    /** 红娘用户编码 */
                        @Schema(description = "红娘用户编码")
                private String mtMkSysUserCode;

                    /** 追求者 */
                        @Schema(description = "追求者")
                private String mtPursuingSysUserCode;

                    /** 被追求者 */
                        @Schema(description = "被追求者")
                private String mtPursuedSysUserCode;

                    /** 是否通过 */
                        @Schema(description = "是否通过")
                private String mtPassedStatusCode;

                    /** 恋爱进程（1联系人、2面基、3牵手） */
                        @Schema(description = "恋爱进程")
                private String mtProcessCode;


        }

package pub.module.dating.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.api.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 我的红娘 对象
 * @author tg
 * 2026-01-07 23:30:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "我的红娘")
public class DtCusMatchmaker extends BaseEntity {
                    /** 所属部门 */
                        @Schema(description = "所属部门")
                private String sysOrgCode;

                    /** 签约编码 */
                        @Schema(description = "签约编码")
                private String cmCode;

                    /** 合同名称 */
                        @Schema(description = "合同名称")
                private String cmName;

                    /** 红娘编码 */
                        @Schema(description = "客户编码")
                private String cmCusCode;

                    /** 客户编码 */
                        @Schema(description = "红娘编码")
                private String cmMtCode;

                    /** 是否通过 */
                        @Schema(description = "是否通过")
                private String cmPassedStatusCode;


        }

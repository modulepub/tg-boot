package pub.module.dating.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 喜欢 对象
 * @author tg
 * 2026-01-07 23:30:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "喜欢")
public class DtLike extends BaseEntity {
                    /** 所属部门 */
                        @Schema(description = "所属部门")
                private String sysOrgCode;

                    /** 编码 */
                        @Schema(description = "编码")
                private String likeCode;

                    /** 名称 */
                        @Schema(description = "名称")
                private String likeName;

                    /** 己方用户编码 */
                        @Schema(description = "己方用户编码")
                private String likeOwnSysUserCode;

                    /** 对方用户编码 */
                        @Schema(description = "对方用户编码")
                private String likeOtherSysUserCode;

                    /** 喜好程度 */
                        @Schema(description = "喜好程度")
                private String likeDegreeCode;


        }

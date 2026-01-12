package pub.module.system.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户所属组织机构 对象
 * @author tg
 * 2026-01-04 13:16:25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户所属组织机构")
public class SysUserOrganization extends BaseEntity {
                    /** 用户编码 */
                        @Schema(description = "用户编码")
                private String userCode;

                    /** 编码 */
                        @Schema(description = "编码")
                private String userOrgCode;


        }

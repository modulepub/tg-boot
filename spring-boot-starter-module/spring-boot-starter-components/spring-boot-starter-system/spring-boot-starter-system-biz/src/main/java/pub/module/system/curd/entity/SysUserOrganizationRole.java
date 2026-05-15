package pub.module.system.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户所属角色表 对象
 * @author tg
 * 2026-01-04 13:16:25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户所属角色表")
public class SysUserOrganizationRole extends BaseEntity {
                    /** 用户编码 */
                        @Schema(description = "用户编码")
                private String userCode;

                    /** 角色编码 */
                        @Schema(description = "角色编码")
                private String roleCode;

                    /** 编码 */
                        @Schema(description = "编码")
                private String userOrgRoleCode;


        }

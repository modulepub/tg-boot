package pub.module.system.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 角色权限表 对象
 * @author tg
 * 2026-01-04 13:16:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "角色权限表")
public class SysRolePermission extends BaseEntity {
                    /** 权限编码 */
                        @Schema(description = "权限编码")
                private String perCode;

                    /** 角色编码 */
                        @Schema(description = "角色编码")
                private String roleCode;

                    /** 编码 */
                        @Schema(description = "编码")
                private String rolePerCode;


        }

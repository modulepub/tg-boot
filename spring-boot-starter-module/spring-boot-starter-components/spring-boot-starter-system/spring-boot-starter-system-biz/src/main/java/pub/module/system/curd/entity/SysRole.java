package pub.module.system.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 角色表 对象
 * @author tg
 * 2026-01-04 13:16:23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "角色表")
public class SysRole extends BaseEntity {
                    /** 角色编码 */
                        @Schema(description = "角色编码")
                private String roleCode;

                    /** 角色名称 */
                        @Schema(description = "角色名称")
                private String roleName;

                    /** 描述 */
                        @Schema(description = "描述")
                private String roleDescription;


        }

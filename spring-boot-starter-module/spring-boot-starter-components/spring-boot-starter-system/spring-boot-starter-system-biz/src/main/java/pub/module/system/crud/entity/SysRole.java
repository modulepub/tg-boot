package pub.module.system.crud.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
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
                    @Schema(description = "角色编码")
                private String roleCode;

                    @Schema(description = "角色名称")
                private String roleName;

                    @Schema(description = "描述")
                private String roleDescription;


        }

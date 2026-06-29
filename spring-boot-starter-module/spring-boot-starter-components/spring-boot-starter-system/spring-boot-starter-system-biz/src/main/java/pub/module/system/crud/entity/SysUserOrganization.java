package pub.module.system.crud.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户所属组织机构 对象
 *
 * @author tg
 * 2026-01-04 13:16:25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户所属组织机构")
public class SysUserOrganization extends BaseEntity {
    @Schema(description = "编码")
    private String userOrgCode;
    @Schema(description = "用户编码")
    private String userCode;

    @Schema(description = "机构编码")
    private String orgCode;

    @Schema(description = "角色编码")
    private String roleCode;


}

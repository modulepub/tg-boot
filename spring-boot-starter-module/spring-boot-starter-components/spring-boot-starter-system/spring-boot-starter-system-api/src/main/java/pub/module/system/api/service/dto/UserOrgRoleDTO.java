package pub.module.system.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户机构角色
 */
@Data
@Schema(description = "用户机构角色")
public class UserOrgRoleDTO {

    @Schema(description = "机构编码")
    private String orgCode;

    @Schema(description = "角色编码")
    private String roleCode;
}

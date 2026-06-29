package pub.module.system.crud.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户角标")
public class SysUserBadge extends BaseEntity {

    @Schema(description = "用户编码")
    private String userCode;

    @Schema(description = "角标 key")
    private String badgeKey;

    @Schema(description = "角标数量")
    private Integer badgeCount;
}

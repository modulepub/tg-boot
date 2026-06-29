package pub.module.system.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户角标")
public class SysUserBadgeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户编码")
    private String userCode;

    @Schema(description = "角标 key")
    private String badgeKey;

    @Schema(description = "角标数量")
    private Integer badgeCount;
}

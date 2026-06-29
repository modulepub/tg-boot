package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理端-设置企业管理员。
 */
@Data
@Schema(description = "设置企业管理员")
public class MatchmakingCompanySetAdminVO {

    @Schema(description = "企业主键 id")
    private String id;

    @Schema(description = "管理员用户编码")
    private String adminUserCode;
}

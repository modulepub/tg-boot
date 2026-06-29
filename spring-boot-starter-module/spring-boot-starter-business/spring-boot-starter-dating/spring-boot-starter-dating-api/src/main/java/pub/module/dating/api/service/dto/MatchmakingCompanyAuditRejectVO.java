package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理端-企业入驻驳回。
 */
@Data
@Schema(description = "企业入驻驳回")
public class MatchmakingCompanyAuditRejectVO {

    @Schema(description = "主键 id")
    private String id;

    @Schema(description = "驳回原因")
    private String rejectReason;
}

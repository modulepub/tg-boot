package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理端-红娘资质驳回。
 */
@Data
@Schema(description = "红娘资质驳回")
public class MatchmakerAuditRejectVO {

    @Schema(description = "主键 id")
    private String id;

    @Schema(description = "驳回原因")
    private String rejectReason;
}

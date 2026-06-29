package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "内容审核人工驳回")
public class ContentModerationRejectVO {

    @Schema(description = "记录 id")
    private String id;

    @Schema(description = "驳回原因")
    private String rejectReason;
}

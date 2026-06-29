package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "资产认证驳回")
public class VtAssetCertRejectVO {

    @Schema(description = "记录 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "驳回原因", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rejectReason;
}

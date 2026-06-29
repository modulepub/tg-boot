package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 单条待检测内容
 */
@Data
@Schema(description = "内容合法校验-待检测项")
public class ContentModerationItemDTO {

    @NotBlank
    @Schema(description = "内容类型：TEXT/IMAGE/VIDEO", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cmRecordContentTypeCode;

    @NotBlank
    @Schema(description = "待检测文本或媒体 URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cmRecordContent;
}

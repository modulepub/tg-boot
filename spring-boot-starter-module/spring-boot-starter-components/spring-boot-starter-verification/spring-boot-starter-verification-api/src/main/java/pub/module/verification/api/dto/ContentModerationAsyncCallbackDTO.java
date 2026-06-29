package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 异步内容安全回调（如微信 wxa_media_check）
 */
@Data
@Schema(description = "内容合法校验-异步回调")
public class ContentModerationAsyncCallbackDTO {

    @NotBlank
    @Schema(description = "上游 trace_id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cmRecordVendorTraceId;

    @Schema(description = "上游检测错误码：0 表示检测成功，非 0 表示检测失败（如 -1008 下载错误）")
    private Integer errCode;

    @Schema(description = "上游错误描述")
    private String errMsg;

    @Schema(description = "微信 suggest：pass/risky/review")
    private String suggest;

    @Schema(description = "备注（第三方响应原文）")
    private String cmRecordRemark;
}

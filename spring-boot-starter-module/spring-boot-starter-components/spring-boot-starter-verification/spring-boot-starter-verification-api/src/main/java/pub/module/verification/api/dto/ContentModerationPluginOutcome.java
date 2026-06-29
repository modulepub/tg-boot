package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 插件单次检测输出（模块内 SPI 与 Api 层边界）
 */
@Data
@Builder
@Schema(description = "内容合法校验-插件检测输出")
public class ContentModerationPluginOutcome {

    @Schema(description = "第三方 API 是否调用成功")
    private boolean apiReachable;

    @Schema(description = "是否异步检测")
    private boolean async;

    @Schema(description = "流程：0待审核 1审核中 2审核结束")
    private String cmRecordProcessCode;

    @Schema(description = "是否通过：1是 0否；流程未结束时可为空")
    private String cmRecordPassedStatusCode;

    @Schema(description = "未通过原因")
    private String cmRecordNotPassedReason;

    @Schema(description = "插件编码")
    private String cmRecordPluginCode;

    @Schema(description = "上游 trace_id")
    private String cmRecordVendorTraceId;

    @Schema(description = "备注（第三方原始结果或错误说明）")
    private String cmRecordRemark;
}

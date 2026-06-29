package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 单条内容检测结果
 */
@Data
@Builder
@Schema(description = "内容合法校验-单条结果")
public class ContentModerationItemResult {

    private String id;
    private String cmRecordCode;
    private String cmRecordContentTypeCode;
    private String cmRecordContent;
    private String cmRecordPluginCode;
    private String cmRecordProcessCode;
    private String cmRecordPassedStatusCode;
    @Schema(description = "未通过原因")
    private String cmRecordNotPassedReason;
    private String cmRecordAsyncStatusCode;
    private String cmRecordVendorTraceId;
    private String cmRecordRemark;
}

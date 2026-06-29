package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 内容审核记录流程结束事件（异步回调或人工审核后）。
 */
@Data
@Builder
@Schema(description = "内容审核记录流程结束事件")
public class ContentModerationRecordFinishedDTO {

    private String cmRecordCode;
    private String cmRecordSourceModuleCode;
    private String cmRecordBizCode;
    private String cmRecordContentTypeCode;
    private String cmRecordProcessCode;
    private String cmRecordPassedStatusCode;
    private String cmRecordNotPassedReason;
}

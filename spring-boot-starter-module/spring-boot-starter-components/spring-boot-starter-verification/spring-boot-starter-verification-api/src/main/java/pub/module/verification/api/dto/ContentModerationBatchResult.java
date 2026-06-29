package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 批量内容检测结果
 */
@Data
@Builder
@Schema(description = "内容合法校验-批量结果")
public class ContentModerationBatchResult {

    @Schema(description = "是否允许业务继续（同步项均通过且异步项已成功提交）")
    private boolean passed;

    @Schema(description = "阻断原因（passed=false 时）")
    private String blockedMessage;

    @Schema(description = "各条检测明细")
    private List<ContentModerationItemResult> items;
}

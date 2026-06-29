package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 红娘视频号配置。
 */
@Data
@Schema(description = "红娘视频号配置")
public class MatchmakerChannelsDTO {

    @Schema(description = "红娘编码")
    private String mkCode;

    @Schema(description = "视频号 id")
    private String mkChannelsFinderUserName;

    @Schema(description = "视频号是否已生效（StatusCode：1已生效 0未生效）")
    private String mkChannelsAuditStatusCode;

    @Schema(description = "视频号审核流程（ProcessCode：0待提交 1待审核 2审核通过 3审核失败）")
    private String mkChannelsProcessCode;

    @Schema(description = "视频号审核失败原因")
    private String mkChannelsRejectReason;

    @Schema(description = "小程序是否展示视频号入口")
    private boolean channelsEnabled;

    @Schema(description = "生效状态说明")
    private String statusLabel;

    @Schema(description = "审核流程说明")
    private String processStatusLabel;

    @Schema(description = "是否可提交审核")
    private boolean canSubmit;
}

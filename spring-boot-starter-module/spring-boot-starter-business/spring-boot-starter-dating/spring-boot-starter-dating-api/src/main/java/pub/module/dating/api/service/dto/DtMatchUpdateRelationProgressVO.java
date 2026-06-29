package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pub.module.dating.api.constants.MatchRelationProgressCodeEnum;

/**
 * 红娘更新牵线关系进度
 */
@Data
@Schema(description = "牵线关系进度更新")
public class DtMatchUpdateRelationProgressVO {

    @Schema(description = "牵线记录主键 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "关系进度", requiredMode = Schema.RequiredMode.REQUIRED)
    private MatchRelationProgressCodeEnum mtRelationProgressCode;

    @Schema(description = "见面截图 URL，逗号分隔")
    private String mtMeetingScreenshot;

    @Schema(description = "聊天截图 URL，逗号分隔")
    private String mtChatScreenshot;
}

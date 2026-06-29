package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 红娘视频号修改。
 */
@Data
@Schema(description = "红娘视频号修改")
public class MatchmakerChannelsUpdateVO {

    @Schema(description = "视频号 id（finderUserName，sph 开头；留空表示清除）")
    private String mkChannelsFinderUserName;
}

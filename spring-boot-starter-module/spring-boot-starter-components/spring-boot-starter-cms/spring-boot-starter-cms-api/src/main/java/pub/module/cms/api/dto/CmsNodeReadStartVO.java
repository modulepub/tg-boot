package pub.module.cms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CMS-开始阅读响应")
public class CmsNodeReadStartVO {

    @Schema(description = "单次阅读会话编码，后续上报进度时携带")
    private String sessionCode;

    @Schema(description = "当前进度")
    private Integer progress;

    @Schema(description = "累计阅读数")
    private Long nodeViewCount;
}

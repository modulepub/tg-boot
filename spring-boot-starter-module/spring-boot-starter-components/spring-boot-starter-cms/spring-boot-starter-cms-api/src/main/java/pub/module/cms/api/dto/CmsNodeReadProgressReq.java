package pub.module.cms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CMS-阅读进度上报请求")
public class CmsNodeReadProgressReq {

    @Schema(description = "单次阅读会话编码")
    private String sessionCode;

    @Schema(description = "阅读进度 0-100")
    private Integer progress;
}

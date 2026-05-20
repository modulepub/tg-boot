package pub.module.cms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "短链解析结果")
public class CmsShortUrlResolveVO {
    @Schema(description = "短码")
    private String shortUrlKey;
    @Schema(description = "目标 path+query")
    private String shortUrlTarget;
    @Schema(description = "标题")
    private String shortUrlTitle;
}

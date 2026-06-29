package pub.module.cms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建短链请求")
public class CmsShortUrlCreateReq {
    @Schema(description = "目标小程序 path+query，勿带前导 /")
    private String shortUrlTarget;
    @Schema(description = "备注标题")
    private String shortUrlTitle;
}

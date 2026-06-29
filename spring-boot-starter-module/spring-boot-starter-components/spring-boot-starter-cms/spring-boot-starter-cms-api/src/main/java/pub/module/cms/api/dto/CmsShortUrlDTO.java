package pub.module.cms.api.dto;

import pub.module.common.enums.StatusCodeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "CMS-短链")
public class CmsShortUrlDTO {
    @Schema(description = "ID")
    private String id;
    @Schema(description = "业务编码")
    private String shortUrlCode;
    @Schema(description = "短码")
    private String shortUrlKey;
    @Schema(description = "目标 path+query")
    private String shortUrlTarget;
    @Schema(description = "标题")
    private String shortUrlTitle;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "过期时间")
    private Date shortUrlExpireTime;
    @Schema(description = "状态")
    private StatusCodeEnum shortUrlStatusCode;
    @Schema(description = "点击次数")
    private Long shortUrlClickCount;
}

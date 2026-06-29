package pub.module.cms.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CMS-开始阅读请求")
public class CmsNodeReadStartReq {

    @Schema(description = "文章节点主键 id")
    private String nodeId;

    @Schema(description = "阅读用户编码（前端登录后传入）")
    private String userCode;
}

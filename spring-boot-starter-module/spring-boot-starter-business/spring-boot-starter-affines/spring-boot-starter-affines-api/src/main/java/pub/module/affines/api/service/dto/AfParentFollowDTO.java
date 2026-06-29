package pub.module.affines.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pub.module.common.enums.StatusCodeEnum;

@Data
@Schema(description = "家长关注")
public class AfParentFollowDTO {

    @Schema(description = "关注记录编码")
    private String afParentFollowCode;

    @Schema(description = "关注者用户编码")
    private String afFollowerUserCode;

    @Schema(description = "被关注资料卡编码")
    private String afTargetChildProfileCode;

    @Schema(description = "是否关注")
    private StatusCodeEnum afFollowStatusCode;

    @Schema(description = "被关注资料卡摘要")
    private AfChildProfileDTO targetProfile;
}

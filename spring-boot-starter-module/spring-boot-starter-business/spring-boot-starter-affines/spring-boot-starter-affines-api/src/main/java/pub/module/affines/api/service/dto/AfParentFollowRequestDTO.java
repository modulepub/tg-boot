package pub.module.affines.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "关注孩子资料卡请求")
public class AfParentFollowRequestDTO {

    @Schema(description = "被关注资料卡编码")
    private String afTargetChildProfileCode;
}

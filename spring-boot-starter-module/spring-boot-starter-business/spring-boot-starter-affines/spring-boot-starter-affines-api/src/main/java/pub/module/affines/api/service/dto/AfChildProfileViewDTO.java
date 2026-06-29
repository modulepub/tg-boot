package pub.module.affines.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "孩子资料卡浏览记录")
public class AfChildProfileViewDTO {

    @Schema(description = "浏览记录编码")
    private String afChildProfileViewCode;

    @Schema(description = "被浏览资料卡编码")
    private String afChildProfileCode;

    @Schema(description = "浏览者用户编码")
    private String afViewerUserCode;

    @Schema(description = "浏览时间")
    private LocalDateTime createTime;

    @Schema(description = "被浏览资料卡摘要")
    private AfChildProfileDTO profile;
}

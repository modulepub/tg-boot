package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 资质申请可选婚介公司（已审核通过）。
 */
@Data
@Schema(description = "已审核婚介公司选项")
public class MatchmakingCompanyOptionDTO {

    @Schema(description = "婚介公司编码")
    private String mkCompanyCode;

    @Schema(description = "婚介公司名称")
    private String mkCompanyName;
}

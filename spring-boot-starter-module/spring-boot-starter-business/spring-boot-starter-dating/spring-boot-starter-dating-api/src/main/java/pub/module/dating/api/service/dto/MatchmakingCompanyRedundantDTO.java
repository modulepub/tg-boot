package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 婚介公司冗余快照（同步至红娘表等企业关联表）。
 */
@Data
@Schema(description = "婚介公司冗余快照")
public class MatchmakingCompanyRedundantDTO {

    @Schema(description = "婚介所编码")
    private String mkCompanyCode;

    @Schema(description = "婚介所名称")
    private String mkCompanyName;
}

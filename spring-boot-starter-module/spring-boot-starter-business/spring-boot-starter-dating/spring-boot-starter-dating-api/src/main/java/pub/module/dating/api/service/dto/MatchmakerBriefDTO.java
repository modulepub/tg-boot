package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 红娘简要信息（跨模块 API 契约）
 */
@Data
@Schema(description = "红娘简要信息")
public class MatchmakerBriefDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "红娘编码")
    private String mkCode;

    @Schema(description = "红娘姓名")
    private String mkName;

    @Schema(description = "红娘绑定的 system userCode")
    private String mkUserCode;
}

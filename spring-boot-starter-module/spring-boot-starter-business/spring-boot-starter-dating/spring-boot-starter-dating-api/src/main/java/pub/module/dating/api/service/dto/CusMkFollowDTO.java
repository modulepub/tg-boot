package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户端：关注红娘（建立客户-红娘关系）
 */
@Data
@Schema(description = "关注红娘参数")
public class CusMkFollowDTO {

    @Schema(description = "红娘编码 dt_matchmaker.mk_code")
    private String mkCode;
}

package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户端：更新客户-红娘关系「主页展示」状态
 */
@Data
@Schema(description = "客户红娘关系主页展示更新参数")
public class CusMkRelShowStatusUpdateDTO {

    @Schema(description = "客户红娘关系编码 cusMkRelCode")
    private String cusMkRelCode;

    /** {@code 1} 展示；{@code 0} 不展示（与字典/前端约定一致） */
    @Schema(description = "是否在红娘主页展示：1-展示 0-不展示")
    private String cusMkRelShowStatusCode;
}

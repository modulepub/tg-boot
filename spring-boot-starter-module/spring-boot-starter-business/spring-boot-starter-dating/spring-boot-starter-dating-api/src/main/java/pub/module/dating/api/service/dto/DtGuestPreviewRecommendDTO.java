package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 免登录推荐预览单条：嘉宾快照 + 与访客意向的匹配分。
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class DtGuestPreviewRecommendDTO implements Serializable {

    @Schema(description = "嘉宾客户快照")
    private DtCustomerDTO customer;

    @Schema(description = "与访客意向的匹配分 0～100")
    private BigDecimal recommendedMatchScore;
}

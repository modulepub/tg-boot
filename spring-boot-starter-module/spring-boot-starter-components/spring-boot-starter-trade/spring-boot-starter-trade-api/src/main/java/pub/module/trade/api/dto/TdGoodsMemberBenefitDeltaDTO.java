package pub.module.trade.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 会员三类权益累加值（每日次数上限增量）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "商品会员权益增量")
public class TdGoodsMemberBenefitDeltaDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "添加好友权益增量")
    private long addFriendNum;

    @Schema(description = "推荐权益增量")
    private long recNum;

    @Schema(description = "牵线权益增量")
    private long matchNum;
}

package pub.module.customer.curd.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * 会员权益充值记录
 *
 * @author tg
 * @since 2026-05-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "会员权益充值记录")
public class CustomerMemberBenefitRechargeRecord extends BaseEntity {

    @Schema(description = "权益充值记录编号")
    private String cusMbRchRecordCode;

    @Schema(description = "订单商品明细编码")
    private String tdOdGdCode;

    @Schema(description = "订单编码")
    private String tdOdCode;

    @Schema(description = "系统用户编码")
    private String userCode;

    @Schema(description = "客户编号")
    private String cusCode;

    @Schema(description = "添加好友权益增量")
    private Long cusAddFriendRightDelta;

    @Schema(description = "推荐权益增量")
    private Long cusRecommendRightDelta;

    @Schema(description = "牵线权益增量")
    private Long cusMatchRightDelta;
}

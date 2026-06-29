package pub.module.distribution.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dist_user_bill_summary")
@Schema(description = "分销用户账单汇总")
public class DistUserBillSummary extends BaseEntity {

    @Schema(description = "汇总流水编码")
    private String distUserBillSummaryCode;

    @Schema(description = "用户编码（账单所属用户）")
    private String distUserCode;

    @Schema(description = "用户昵称（冗余）")
    private String distUserNickName;

    @Schema(description = "用户真实姓名（冗余）")
    private String distUserRealName;

    @Schema(description = "邀请人用户编码（冗余）")
    private String distInviterUserCode;

    @Schema(description = "邀请人昵称（冗余）")
    private String distInviterUserNickName;

    @Schema(description = "邀请人真实姓名（冗余）")
    private String distInviterUserRealName;

    @Schema(description = "业务线编码")
    private String distBizLineCode;

    @Schema(description = "付费总金额（本人消费）")
    private BigDecimal distPaidTotalAmount;

    @Schema(description = "服务期内总金额（含服务期商品的下单金额）")
    private BigDecimal distInServiceTotalAmount;

    @Schema(description = "子级用户付费总金额")
    private BigDecimal distSubPaidTotalAmount;

    @Schema(description = "子级服务期内总金额")
    private BigDecimal distSubInServiceTotalAmount;

    @Schema(description = "是否测试数据（0否 1是）")
    private String distTestStatusCode;
}

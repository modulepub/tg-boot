package pub.module.distribution.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dist_user_bill_settle_record")
@Schema(description = "用户账单结算记录")
public class DistUserBillSettleRecord extends BaseEntity {

    @Schema(description = "结算记录流水编码")
    private String distUserBillSettleRecordCode;

    @Schema(description = "业务线编码")
    private String distBizLineCode;

    @Schema(description = "付款用户编码")
    private String distPayerUserCode;

    @Schema(description = "下单人用户编码（冗余）")
    private String tdOdSysUserCode;

    @Schema(description = "下单人姓名（冗余）")
    private String tdOdSysUserRealName;

    @Schema(description = "订单商品编码")
    private String tdOdGdCode;

    @Schema(description = "商品编码")
    private String tdGdCode;

    @Schema(description = "商品名称")
    private String tdGdName;

    @Schema(description = "分佣比例（冗余）")
    private BigDecimal tdGdCommissionRate;

    @Schema(description = "付费金额")
    private BigDecimal distPaidAmount;

    @Schema(description = "分佣池金额（付费×商品分佣比例）")
    private BigDecimal distCommissionPoolAmount;

    @Schema(description = "直推邀请人用户编码")
    private String distInviterUserCode;

    @Schema(description = "直推邀请人分佣金额")
    private BigDecimal distInviterCommissionAmount;

    @Schema(description = "上级邀请人用户编码")
    private String distSuperiorInviterUserCode;

    @Schema(description = "上级邀请人分佣金额（付费5%）")
    private BigDecimal distSuperiorCommissionAmount;

    @Schema(description = "分佣是否已计入汇总（StatusCode：0否 1是）")
    private String distCommissionAppliedStatusCode;

    @Schema(description = "是否结算完成（StatusCode：0否 1是）")
    private String distSettledStatusCode;

    @Schema(description = "是否申请结算（StatusCode：0否 1是）")
    private String distSettleAppliedStatusCode;

    @Schema(description = "是否在服务期内（StatusCode：0否 1是）")
    private String distInServiceStatusCode;

    @Schema(description = "服务期结束时间")
    private LocalDateTime distServicePeriodEndAt;

    @Schema(description = "实际结算时间")
    private LocalDateTime distSettledAt;

    @Schema(description = "结算批次编码")
    private String distSettleBatchCode;

    @Schema(description = "结算批次状态冗余（StatusCode：0未完成 1已完成）")
    private String distSettleBatchStatusCode;

    @Schema(description = "是否测试数据（0否 1是）")
    private String distTestStatusCode;
}

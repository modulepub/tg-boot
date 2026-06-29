package pub.module.distribution.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dist_user_bill_event")
@Schema(description = "用户账单汇总事件（幂等）")
public class DistUserBillEvent extends BaseEntity {

    @Schema(description = "事件流水编码")
    private String distUserBillEventCode;

    @Schema(description = "业务线编码")
    private String distBizLineCode;

    @Schema(description = "事件来源类型")
    private String distBillEventSourceTypeCode;

    @Schema(description = "事件来源 ID（如订单商品编码）")
    private String distBillEventSourceId;

    @Schema(description = "付款用户编码")
    private String distPayerUserCode;

    @Schema(description = "是否测试数据（0否 1是）")
    private String distTestStatusCode;
}

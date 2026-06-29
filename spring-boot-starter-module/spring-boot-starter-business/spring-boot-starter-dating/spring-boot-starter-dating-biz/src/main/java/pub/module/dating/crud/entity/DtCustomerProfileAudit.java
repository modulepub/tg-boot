package pub.module.dating.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * 客户资料审核明细（按 用户 + 字段 + 子项 存放待审核值与审核状态，替代原整行快照编辑表）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dt_customer_profile_audit")
@Schema(description = "客户资料审核明细")
public class DtCustomerProfileAudit extends BaseEntity {

    @Schema(description = "资料审核业务编码")
    private String cusProfileAuditCode;

    @Schema(description = "所属用户编码")
    private String cusUserCode;

    @Schema(description = "客户字段名（Java 属性）")
    private String cusProfileAuditFieldName;

    @Schema(description = "多值字段子项序号")
    private Integer cusProfileAuditFieldItemIndex;

    @Schema(description = "该字段/子项提交的待审核值")
    private String cusProfileAuditPendingValue;

    @Schema(description = "内容审核记录业务编码")
    private String cmRecordCode;

    @Schema(description = "审核流程：0待审核 1审核中 2结束")
    private String cusProfileAuditProcessCode;

    @Schema(description = "是否通过：1是 0否")
    private String cusProfileAuditPassedStatusCode;

    @Schema(description = "未通过提示")
    private String cusProfileAuditNotPassedTip;
}

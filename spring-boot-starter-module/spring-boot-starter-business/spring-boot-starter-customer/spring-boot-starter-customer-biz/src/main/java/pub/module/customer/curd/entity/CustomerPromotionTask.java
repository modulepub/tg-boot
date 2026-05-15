package pub.module.customer.curd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 客户营销关系 对象
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户营销关系")
public class CustomerPromotionTask extends BaseEntity {


    @Schema(description = "任务编码")
    private String promotionTaskCode;

    @Schema(description = "营销任务类型编码")
    private String promotionTaskTypeCode;

    @Schema(description = "客户编码")
    private String cusCode;

    @Schema(description = "用户编号")
    private String userCode;
    @Schema(description = "用户姓名")
    private String userRealName;

    @Schema(description = "手机号")
    private String cusPhone;

    @Schema(description = "客户姓名")
    private String cusName;

    @Schema(description = "身份证号")
    private String cusIdCardNum;

    @Schema(description = "是否跟进")
    private String cusFollowUpStatusCode;

    @Schema(description = "是否成交")
    private String cusDealtStatusCode;
    @Schema(description = "是否完单")
    private String cusDealtCompleteStatusCode;

    @TableField(exist = false)
    @Schema(description = "创建日期检索")
    private String[] createDateRangeArray;
    @Schema(description = "是否意向")
    private String cusIntentionStatusCode;

    @Schema(description = "意向等级")
    private String cusIntentionLevelCode;

}

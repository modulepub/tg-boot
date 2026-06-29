package pub.module.verification.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import pub.module.common.model.po.BaseEntity;
import pub.module.verification.api.constants.CmRecordProcessCodeEnum;

import java.time.LocalDateTime;

/**
 * 内容合法校验审核记录
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "内容合法校验审核记录")
@TableName("vt_cm_record")
public class CmRecord extends BaseEntity {

    @Schema(description = "内容审核记录编码")
    private String cmRecordCode;

    @Schema(description = "发起方业务模块编码")
    private String cmRecordSourceModuleCode;

    @Schema(description = "发起方业务主体编码")
    private String cmRecordBizCode;

    @Schema(description = "发起方用户编码")
    private String cmRecordUserCode;

    @Schema(description = "发起方用户姓名（冗余，便于展示）")
    private String cmRecordUserName;

    @Schema(description = "内容类型 TEXT/IMAGE/VIDEO")
    private String cmRecordContentTypeCode;

    @Schema(description = "审核内容（文本或媒体 URL）")
    private String cmRecordContent;

    @Schema(description = "审核插件编码；空表示纯人工审核")
    private String cmRecordPluginCode;

    @Schema(description = "是否通过：1是 0否；流程未结束时为空")
    private String cmRecordPassedStatusCode;

    @Schema(description = "未通过原因（校验未通过或人工驳回时填写）")
    private String cmRecordNotPassedReason;

    @Schema(description = "是否异步：1是 0否")
    private String cmRecordAsyncStatusCode;

    @Schema(description = "审核流程：0待审核 1审核中 2审核结束")
    private CmRecordProcessCodeEnum cmRecordProcessCode;

    @Schema(description = "备注（第三方插件原始结果或人工驳回说明）")
    private String cmRecordRemark;

    @Schema(description = "上游 trace_id（异步回调匹配）")
    private String cmRecordVendorTraceId;

    @Schema(description = "人工审核人用户编码")
    private String cmRecordAuditBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "人工审核时间")
    private LocalDateTime cmRecordAuditAt;
}

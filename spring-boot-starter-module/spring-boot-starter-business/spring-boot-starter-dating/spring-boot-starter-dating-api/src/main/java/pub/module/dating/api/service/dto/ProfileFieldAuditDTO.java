package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 资料编辑单字段审核结果（App 展示未通过原因）
 */
@Data
@Schema(description = "资料编辑字段审核结果")
public class ProfileFieldAuditDTO {

    @Schema(description = "审核流程：0待审核 1审核中 2结束")
    private String auditProcessCode;

    @Schema(description = "是否通过：1是 0否；流程未结束时为空")
    private String auditPassedStatusCode;

    @Schema(description = "未通过提示")
    private String auditNotPassedTip;
}

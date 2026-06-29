package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业管理员-红娘资质审核通过（需上传附件）。
 */
@Data
@Schema(description = "企业红娘审核通过")
public class EnterpriseStaffAuditApproveVO {

    @Schema(description = "主键 id")
    private String id;

    @Schema(description = "视频承诺文件")
    private String mkVideoCommitmentFile;

    @Schema(description = "红娘服务协议文件")
    private String mkServiceAgreementFile;
}

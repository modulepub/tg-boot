package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 企业管理员-旗下红娘列表项。
 */
@Data
@Schema(description = "企业旗下红娘")
public class EnterpriseStaffDTO {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "红娘编码")
    private String mkCode;

    @Schema(description = "工作照")
    private String mkWorkPhoto;

    @Schema(description = "红娘姓名")
    private String mkName;

    @Schema(description = "年龄")
    private Integer mkAge;

    @Schema(description = "联系电话")
    private String mkPhone;

    @Schema(description = "所在城市")
    private String mkCityName;

    @Schema(description = "标签，逗号分隔")
    private String mkTags;

    @Schema(description = "说说")
    private String mkMoment;

    @Schema(description = "是否已通过资质认证（StatusCode：1是 0否）")
    private String mkIdentityStatusCode;

    @Schema(description = "资质申请流程（ProcessCode：0待提交 1审核中 2审核通过 3审核拒绝）")
    private String mkIdentityProcessCode;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "用户号")
    private String mkUserCode;

    @Schema(description = "证件号")
    private String mkIdNo;

    @Schema(description = "婚介所名称")
    private String mkCompanyName;

    @Schema(description = "视频号")
    private String mkChannelsFinderUserName;

    @Schema(description = "资质驳回原因")
    private String mkIdentityRejectReason;

    @Schema(description = "资质审核人")
    private String mkIdentityAuditBy;

    @Schema(description = "资质审核时间")
    private LocalDateTime mkIdentityAuditAt;

    @Schema(description = "视频承诺文件")
    private String mkVideoCommitmentFile;

    @Schema(description = "红娘服务协议文件")
    private String mkServiceAgreementFile;

    @Schema(description = "企业审核人")
    private String mkEnterpriseAuditBy;

    @Schema(description = "企业审核时间")
    private LocalDateTime mkEnterpriseAuditAt;

    @Schema(description = "平台驳回原因")
    private String mkPlatformRejectReason;

    @Schema(description = "平台审核人")
    private String mkPlatformAuditBy;

    @Schema(description = "平台审核时间")
    private LocalDateTime mkPlatformAuditAt;
}

package pub.module.dating.crud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.IdentityApplyProcessCodeEnum;
import pub.module.common.model.po.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 红娘信息 对象
 *
 * @author tg
 * 2026-03-22 13:32:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "红娘信息")
public class DtMatchmaker extends BaseEntity {
    @Schema(description = "用户号")
    private String mkUserCode;

    private String mkCode;


    @Schema(description = "工作照")
    private String mkWorkPhoto;

    @Schema(description = "红娘姓名")
    private String mkName;

    @Schema(description = "年龄")
    private Integer mkAge;

    @Schema(description = "服务人数")
    private Long mkServiceUserCount;
    @Schema(description = "电话")
    private String mkPhone;
    @Schema(description = "擅长")
    private String mkTags;
    @Schema(description = "证件号")
    private String mkIdNo;

    @Schema(description = "所属城市")
    private String mkCityCode;
    private String mkCityName;

    @Schema(description = "婚介所编码")
    private String mkCompanyCode;

    @Schema(description = "婚介所名称")
    private String mkCompanyName;

    @Schema(description = "说说")
    private String mkMoment;
    @Schema(description = "是否已通过资质认证（StatusCode：1是 0否，null视同否）")
    private StatusCodeEnum mkIdentityStatusCode;

    @Schema(description = "资质申请流程（ProcessCode）")
    private IdentityApplyProcessCodeEnum mkIdentityProcessCode;
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

    @Schema(description = "视频号 id（finderUserName，以 sph 开头）")
    private String mkChannelsFinderUserName;

    @Schema(description = "视频号是否已生效（StatusCode：1已生效 0未生效）")
    private StatusCodeEnum mkChannelsAuditStatusCode;

    @Schema(description = "视频号审核流程（ProcessCode：0待提交 1待审核 2审核通过 3审核失败）")
    private IdentityApplyProcessCodeEnum mkChannelsProcessCode;

    @Schema(description = "视频号审核失败原因")
    private String mkChannelsRejectReason;

    @Schema(description = "视频号审核人")
    private String mkChannelsAuditBy;

    @Schema(description = "视频号审核时间")
    private LocalDateTime mkChannelsAuditAt;

    @Schema(description = "评分")
    private BigDecimal mkScore;

    @Schema(description = "是否测试数据（StatusCode：1是 0否）")
    private StatusCodeEnum mkTestStatusCode;

}

package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 红娘资质申请（查询/提交结果）。
 */
@Data
@Schema(description = "红娘资质申请")
public class MatchmakerQualificationApplyDTO {

    @Schema(description = "是否存在申请记录")
    private boolean hasRecord;

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

    @Schema(description = "证件号")
    private String mkIdNo;

    @Schema(description = "所属婚介公司编码")
    private String mkCompanyCode;

    @Schema(description = "所属婚介公司名称")
    private String mkCompanyName;

    @Schema(description = "所在城市编码")
    private String mkCityCode;

    @Schema(description = "所在城市")
    private String mkCityName;

    @Schema(description = "说说")
    private String mkMoment;

    @Schema(description = "擅长，逗号分隔")
    private String mkTags;

    @Schema(description = "视频号 id")
    private String mkChannelsFinderUserName;

    @Schema(description = "是否已通过资质认证（StatusCode：1是 0否）")
    private String mkIdentityStatusCode;

    @Schema(description = "资质申请流程（ProcessCode：0待提交 1审核中 2审核通过 3审核拒绝）")
    private String mkIdentityProcessCode;

    @Schema(description = "流程状态文案")
    private String auditStatusLabel;

    @Schema(description = "是否审核通过")
    private boolean certified;

    @Schema(description = "是否审核中")
    private boolean pending;

    @Schema(description = "是否审核拒绝")
    private boolean rejected;

    @Schema(description = "是否待提交")
    private boolean draft;
}

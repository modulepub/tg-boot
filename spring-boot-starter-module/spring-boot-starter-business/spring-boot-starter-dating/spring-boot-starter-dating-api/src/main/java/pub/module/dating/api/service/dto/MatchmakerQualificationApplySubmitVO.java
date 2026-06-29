package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 红娘资质申请提交。
 */
@Data
@Schema(description = "红娘资质申请提交")
public class MatchmakerQualificationApplySubmitVO {

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
}

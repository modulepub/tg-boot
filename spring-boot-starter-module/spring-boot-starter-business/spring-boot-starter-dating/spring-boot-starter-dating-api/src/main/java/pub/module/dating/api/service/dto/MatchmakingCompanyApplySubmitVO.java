package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业入驻申请提交。
 */
@Data
@Schema(description = "企业入驻申请提交")
public class MatchmakingCompanyApplySubmitVO {

    @Schema(description = "婚介所名称")
    private String mkCompanyName;

    @Schema(description = "公司电话")
    private String mkCompanyTel;

    @Schema(description = "统一社会信用代码")
    private String mkCompanyUsciCode;

    @Schema(description = "法人姓名")
    private String mkCompanyLegalName;

    @Schema(description = "法人证件号")
    private String mkCompanyLegalIdNo;

    @Schema(description = "公司地址")
    private String mkCompanyAddressDetail;

    @Schema(description = "所在城市编码")
    private String mkCompanyCityCode;

    @Schema(description = "所在城市名称")
    private String mkCompanyCityName;

    @Schema(description = "办公/门头照片，逗号分隔")
    private String mkCompanyPhotos;

    @Schema(description = "对公银行账号")
    private String mkCompanyPublicAccountNo;

    @Schema(description = "开户行")
    private String mkCompanyBankName;

    @Schema(description = "开户地")
    private String mkCompanyBankLocation;
}

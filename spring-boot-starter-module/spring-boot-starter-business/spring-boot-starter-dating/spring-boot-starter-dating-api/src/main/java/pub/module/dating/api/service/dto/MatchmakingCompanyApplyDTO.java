package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 企业入驻申请（查询/提交结果）。
 */
@Data
@Schema(description = "企业入驻申请")
public class MatchmakingCompanyApplyDTO {

    @Schema(description = "是否存在申请记录")
    private boolean hasRecord;

    @Schema(description = "婚介所编码")
    private String mkCompanyCode;

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

    @Schema(description = "随机认证金额（元）")
    private BigDecimal mkCompanyVerifyAmount;

    @Schema(description = "是否已通过企业认证（StatusCode：1是 0否）")
    private String mkCompanyIdentityStatusCode;

    @Schema(description = "入驻申请流程（ProcessCode：0待提交 1审核中 2审核通过 3审核拒绝）")
    private String mkCompanyIdentityProcessCode;

    @Schema(description = "流程状态文案")
    private String auditStatusLabel;

    @Schema(description = "是否待提交")
    private boolean draft;

    @Schema(description = "是否已认证")
    private boolean certified;

    @Schema(description = "是否审核中")
    private boolean pending;

    @Schema(description = "是否已驳回（可修改重提）")
    private boolean rejected;

    @Schema(description = "驳回原因")
    private String mkCompanyRejectReason;

    @Schema(description = "提交后是否需向平台公户转账认证")
    private boolean needTransfer;

    @Schema(description = "是否已确认转账（StatusCode：1是 0否）")
    private String mkCompanyTransferStatusCode;

    @Schema(description = "是否已确认转账")
    private boolean transferred;

    @Schema(description = "转账状态文案")
    private String transferStatusLabel;

    @Schema(description = "平台收款公司名称")
    private String platformCompanyName;

    @Schema(description = "平台收款公户账号")
    private String platformPublicAccountNo;

    @Schema(description = "平台收款开户行")
    private String platformBankName;

    @Schema(description = "平台收款开户地")
    private String platformBankLocation;
}

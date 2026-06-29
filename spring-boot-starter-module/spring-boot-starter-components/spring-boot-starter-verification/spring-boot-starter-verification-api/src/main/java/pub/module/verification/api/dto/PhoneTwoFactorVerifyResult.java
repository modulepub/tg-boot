package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 手机号二要素核验结果（与落库主字段一致，便于业务方使用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "手机号二要素核验结果")
public class PhoneTwoFactorVerifyResult {

    @Schema(description = "记录主键")
    private String id;

    @Schema(description = "业务流水号 npRecordCode")
    private String npRecordCode;

    @Schema(description = "发起方业务模块编码")
    private String npRecordSourceModuleCode;

    @Schema(description = "发起方业务主体编码，如客户 cusCode")
    private String npRecordBizCode;

    @Schema(description = "手机号")
    private String npRecordPhone;

    @Schema(description = "姓名")
    private String npRecordRealName;

    @Schema(description = "是否通过：1通过 0未通过 E异常；BizCode 见 npRecordVendorBizCode")
    private String npRecordPassedStatusCode;

    @Schema(description = "阿里云 BizCode：1一致 2不一致 3查无（以此为准）")
    private String npRecordVendorBizCode;

    @Schema(description = "提供方编码，如 aliyun_cloudauth")
    private String npRecordProviderCode;

    @Schema(description = "上游请求 ID")
    private String npRecordVendorRequestId;

    @Schema(description = "上游说明/错误信息")
    private String npRecordVendorMessage;

    @Schema(description = "基础运营商")
    private String npRecordBasicCarrier;
}

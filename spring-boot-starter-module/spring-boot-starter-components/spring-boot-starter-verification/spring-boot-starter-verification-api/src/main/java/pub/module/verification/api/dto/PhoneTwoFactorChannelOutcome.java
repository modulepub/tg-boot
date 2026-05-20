package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 内部渠道（如阿里云号码百科）返回的核验结果快照，供落库与三方回调 SPI 使用。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "二要素渠道返回结果")
public class PhoneTwoFactorChannelOutcome {

    @Schema(description = "渠道标识，如 aliyun_cloudauth")
    private String providerCode;

    @Schema(description = "是否拿到可解析的业务响应（非网络/客户端异常）")
    private boolean apiReachable;

    @Schema(description = "上游业务码，如 OK")
    private String vendorCode;

    @Schema(description = "上游描述信息")
    private String vendorMessage;

    @Schema(description = "上游 RequestId")
    private String vendorRequestId;

    @Schema(description = "本系统是否通过：1通过 0未通过（由 BizCode 映射）")
    private String isConsistentCode;

    @Schema(description = "阿里云 ResultObject.BizCode：1一致 2不一致 3查无")
    private String vendorBizCode;

    @Schema(description = "基础运营商")
    private String basicCarrier;

    @Schema(description = "原始响应摘要（可截断）")
    private String rawSummary;
}

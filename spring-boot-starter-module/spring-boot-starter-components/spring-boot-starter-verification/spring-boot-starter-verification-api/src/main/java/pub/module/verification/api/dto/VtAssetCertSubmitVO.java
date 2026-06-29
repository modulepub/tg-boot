package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 爱与诚辅助认证提交
 */
@Data
@Schema(description = "资产认证提交")
public class VtAssetCertSubmitVO {

    @Schema(description = "客户编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cusCode;

    @Schema(description = "行驶证照片", requiredMode = Schema.RequiredMode.REQUIRED)
    private String vehicleLicensePhoto;

    @Schema(description = "房产证照片", requiredMode = Schema.RequiredMode.REQUIRED)
    private String realEstateCertificatePhoto;

    @Schema(description = "婚姻状态证明照片")
    private String maritalStatusProofPhoto;

    @Schema(description = "诚实守信录制视频文件", requiredMode = Schema.RequiredMode.REQUIRED)
    private String honestyVideoFile;
}

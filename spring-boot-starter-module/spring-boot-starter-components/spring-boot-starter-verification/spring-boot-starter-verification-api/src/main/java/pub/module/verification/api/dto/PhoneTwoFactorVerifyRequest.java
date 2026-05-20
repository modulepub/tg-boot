package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 手机号 + 姓名 二要素核验请求
 */
@Data
@Schema(description = "手机号姓名二要素核验请求")
public class PhoneTwoFactorVerifyRequest {

    @NotBlank
    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @NotBlank
    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String realName;

    @Schema(description = "业务流水号；不传则由服务端生成")
    private String npRecordCode;

    /**
     * 发起实名/二要素核验的业务模块编码（如 customer、dating），用于回调 SPI 区分来源
     */
    @NotBlank
    @Schema(description = "发起方业务模块编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "customer")
    private String npRecordSourceModuleCode;

    @Schema(description = "发起方业务主体编码，如客户实名传 cusCode")
    private String npRecordBizCode;
}

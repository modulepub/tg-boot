package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 二要素核验渠道配置 DTO（管理端维护 vt_np_config）。
 */
@Data
@Schema(description = "手机号二要素核验配置 DTO")
public class NpConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "配置编码（主键）")
    private String npConfigCode;

    @Schema(description = "启用：1-是 0-否")
    private String npConfigEnabledCode;

    @Schema(description = "渠道编码，默认 aliyun_cloudauth（实人认证 Mobile2MetaVerify）")
    private String npConfigProviderCode;

    @Schema(description = "AccessKeyId")
    private String npConfigAccessKeyId;

    @Schema(description = "AccessKeySecret")
    private String npConfigAccessKeySecret;

    @Schema(description = "保留字段（Cloudauth 无需授权码）")
    private String npConfigAuthCode;

    @Schema(description = "接入点")
    private String npConfigEndpoint;

    @Schema(description = "ParamType：normal 明文 / md5")
    private String npConfigMask;

    @Schema(description = "备注")
    private String npConfigRemark;
}

package pub.module.verification.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

/**
 * 手机号二要素核验渠道配置，表 vt_np_config。
 */
@Data
@TableName("vt_np_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "手机号二要素核验配置")
public class NpConfig extends BaseEntity {

    @Schema(description = "配置编码（业务主键）")
    private String npConfigCode;

    @Schema(description = "启用：1-是 0-否")
    private String npConfigEnabledCode;

    @Schema(description = "渠道编码")
    private String npConfigProviderCode;

    @Schema(description = "AccessKeyId")
    private String npConfigAccessKeyId;

    @Schema(description = "AccessKeySecret")
    private String npConfigAccessKeySecret;

    @Schema(description = "号码百科授权码（旧版「我的申请」字段，新版控制台可留空）")
    private String npConfigAuthCode;

    @Schema(description = "接入点")
    private String npConfigEndpoint;

    @Schema(description = "传参掩码")
    private String npConfigMask;

    @Schema(description = "备注")
    private String npConfigRemark;
}

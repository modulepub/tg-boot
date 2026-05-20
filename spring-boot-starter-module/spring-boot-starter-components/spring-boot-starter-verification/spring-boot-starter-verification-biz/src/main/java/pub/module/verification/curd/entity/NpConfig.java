package pub.module.verification.curd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 手机号二要素核验渠道配置，表 vt_np_config，主键 np_config_code。
 */
@Data
@TableName("vt_np_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "手机号二要素核验配置")
public class NpConfig implements Serializable {

    @TableId(value = "np_config_code", type = IdType.INPUT)
    @Schema(description = "配置编码（主键）")
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

    @Schema(description = "创建人")
    private String createBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "所属组织")
    private String orgCode;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "序号")
    private Long seqNo;

    @Schema(description = "逻辑删除标识")
    @TableLogic
    private Integer deleted;
}

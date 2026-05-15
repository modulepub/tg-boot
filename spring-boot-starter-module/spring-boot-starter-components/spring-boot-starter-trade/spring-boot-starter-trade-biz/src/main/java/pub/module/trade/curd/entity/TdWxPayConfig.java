package pub.module.trade.curd.entity;

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
 * 微信支付配置，表 td_wx_pay_config，主键 wx_pay_config_code。
 */
@Data
@TableName("td_wx_pay_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title = "td_wx_pay_config", description = "微信支付配置")
public class TdWxPayConfig implements Serializable {

    @TableId(value = "wx_pay_config_code", type = IdType.INPUT)
    @Schema(description = "微信支付配置编码（主键）")
    private String wxPayConfigCode;

    @Schema(description = "微信 AppId")
    private String wxPayConfigAppId;

    @Schema(description = "微信商户号")
    private String wxPayConfigMchId;

    @Schema(description = "APIv3 密钥")
    private String wxPayConfigApiV3Key;

    @Schema(description = "支付结果通知 URL")
    private String wxPayConfigNotifyUrl;

    /** apiclient_key.pem 全文（BEGIN PRIVATE KEY ...），非文件路径 */
    @Schema(description = "商户 API 私钥 PEM 字符串")
    private String wxPayConfigPrivateKey;

    /** apiclient_cert.pem 全文（BEGIN CERTIFICATE ...），非文件路径 */
    @Schema(description = "商户 API 证书 PEM 字符串")
    private String wxPayConfigPrivateCert;

    @Schema(description = "是否沙箱：0-否，1-是")
    private Integer wxPayConfigUseSandbox;

    @Schema(description = "启用状态编码")
    private String wxPayConfigEnabledCode;

    @Schema(description = "备注")
    private String wxPayConfigRemark;

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

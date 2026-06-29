package pub.module.ai.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

import java.math.BigDecimal;

/**
 * AI 接口配置，表 ai_api_config。
 */
@Data
@TableName("ai_api_config")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "AI 接口配置")
public class AiApiConfig extends BaseEntity {

    @Schema(description = "AI 接口配置业务编码")
    private String aiApiConfigCode;

    @Schema(description = "配置名称")
    private String aiApiConfigName;

    @Schema(description = "AI 提供商编码，见 AiProviderCode")
    private String aiProviderCode;

    @Schema(description = "API Base URL（OpenAI 兼容，如 https://api.openai.com/v1）")
    private String aiApiConfigBaseUrl;

    @Schema(description = "API Key")
    private String aiApiConfigApiKey;

    @Schema(description = "默认模型名称")
    private String aiApiConfigDefaultModel;

    @Schema(description = "输入单价（每 1K tokens，元）")
    private BigDecimal aiApiConfigInputPricePer1k;

    @Schema(description = "输出单价（每 1K tokens，元）")
    private BigDecimal aiApiConfigOutputPricePer1k;

    @Schema(description = "启用状态编码：1-是 0-否")
    private String aiApiConfigEnabledCode;

    @Schema(description = "备注")
    private String aiApiConfigRemark;
}

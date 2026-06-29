package pub.module.ai.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

import java.math.BigDecimal;

/**
 * AI 消耗明细，表 ai_usage_record。
 */
@Data
@TableName("ai_usage_record")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "AI 消耗明细")
public class AiUsageRecord extends BaseEntity {

    @Schema(description = "消耗明细业务编码")
    private String aiUsageRecordCode;

    @Schema(description = "用户编码")
    private String userCode;

    @Schema(description = "智能体业务编码")
    private String aiAgentCode;

    @Schema(description = "AI 接口配置编码")
    private String aiApiConfigCode;

    @Schema(description = "会话业务编码")
    private String aiChatSessionCode;

    @Schema(description = "使用的模型")
    private String aiUsageRecordModel;

    @Schema(description = "提示词 token 数")
    private Integer aiUsageRecordPromptTokens;

    @Schema(description = "回复 token 数")
    private Integer aiUsageRecordCompletionTokens;

    @Schema(description = "总 token 数")
    private Integer aiUsageRecordTotalTokens;

    @Schema(description = "输入单价快照（每 1K tokens，元）")
    private BigDecimal aiUsageRecordInputUnitPrice;

    @Schema(description = "输出单价快照（每 1K tokens，元）")
    private BigDecimal aiUsageRecordOutputUnitPrice;

    @Schema(description = "本次消耗总价（元）")
    private BigDecimal aiUsageRecordTotalPrice;

    @Schema(description = "是否成功：1-成功 0-失败")
    private String aiUsageRecordSuccessCode;

    @Schema(description = "错误信息")
    private String aiUsageRecordErrorMessage;
}

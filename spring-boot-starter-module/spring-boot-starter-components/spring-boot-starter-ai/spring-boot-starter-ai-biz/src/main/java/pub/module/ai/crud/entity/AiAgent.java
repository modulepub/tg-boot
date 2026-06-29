package pub.module.ai.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * AI 智能体，表 ai_agent。
 */
@Data
@TableName("ai_agent")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "AI 智能体")
public class AiAgent extends BaseEntity {

    @Schema(description = "智能体业务编码")
    private String aiAgentCode;

    @Schema(description = "智能体名称")
    private String aiAgentName;

    @Schema(description = "人设 / 系统提示词")
    private String aiAgentPersona;

    @Schema(description = "关联 AI 接口配置编码（可选，为空则调用时使用首个已启用配置）")
    private String aiApiConfigCode;

    @Schema(description = "模型名称（为空则使用接口配置默认模型）")
    private String aiAgentModel;

    @Schema(description = "启用状态编码：1-是 0-否")
    private String aiAgentEnabledCode;

    @Schema(description = "备注")
    private String aiAgentRemark;
}

package pub.module.verification.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 内容合法校验请求
 */
@Data
@Schema(description = "内容合法校验请求")
public class ContentModerationRequest {

    @Schema(description = "插件编码，如 wechat_media_check；传空则仅落库待人工审核")
    private String cmRecordPluginCode;

    @NotBlank
    @Schema(description = "发起方业务模块编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "dating")
    private String cmRecordSourceModuleCode;

    @Schema(description = "发起方业务主体编码，如 cusCode")
    private String cmRecordBizCode;

    @Schema(description = "发起方用户编码（微信 openid 等上下文）")
    private String cmRecordUserCode;

    @Schema(description = "发起方用户姓名（冗余落库，便于管理端列表展示）")
    private String cmRecordUserName;

    @Schema(description = "微信小程序 appId；不传则使用首个启用的 wx_mini_config")
    private String wxMaAppId;

    @Schema(description = "微信检测场景：1资料 2评论 3论坛 4社交日志，默认 1")
    private Integer wxSecCheckScene;

    @NotEmpty
    @Valid
    @Schema(description = "待检测项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ContentModerationItemDTO> items;
}

package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理端-订阅消息模板筛选项（来自发送记录 group by）。
 */
@Data
public class WxMaSubscribeTemplateOptionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "微信模板 ID")
    private String templateId;

    @Schema(description = "发送记录条数")
    private Long sendCount;

    @Schema(description = "消息场景展示名")
    private String sceneName;
}

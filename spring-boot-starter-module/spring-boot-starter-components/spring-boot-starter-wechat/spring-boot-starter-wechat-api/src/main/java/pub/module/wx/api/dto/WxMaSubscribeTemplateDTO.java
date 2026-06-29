package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信小程序订阅消息模板 DTO。
 */
@Data
public class WxMaSubscribeTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "模板编码（不可修改）")
    private String wxMaSubscribeTemplateCode;

    @Schema(description = "微信订阅消息模板 ID")
    private String wxMaSubscribeTemplateId;

    @Schema(description = "模板说明（场景名、字段映射等）")
    private String wxMaSubscribeTemplateContent;
}

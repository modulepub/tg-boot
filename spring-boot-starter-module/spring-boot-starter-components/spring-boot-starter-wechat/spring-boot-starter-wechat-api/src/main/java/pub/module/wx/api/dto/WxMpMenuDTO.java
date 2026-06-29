package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信公众号自定义菜单 DTO。
 */
@Data
@Schema(description = "微信公众号自定义菜单 DTO")
public class WxMpMenuDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "公众号配置编码")
    private String wxMpConfigCode;

    @Schema(description = "菜单 JSON，格式：{\"button\":[...]}")
    private String menuJson;
}

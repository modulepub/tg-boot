package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 管理端订阅消息模板测试发送请求。
 */
@Data
public class WxMaSubscribeTemplateSendTestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "模板编码")
    private String wxMaSubscribeTemplateCode;

    @Schema(description = "接收用户 userCode")
    private String userCode;

    @Schema(description = "小程序配置编码，空则取默认启用配置")
    private String wxMiniConfigCode;

    @Schema(description = "点击跳转小程序页面，不含开头 /")
    private String page;

    @Schema(description = "模板字段 key -> value")
    private Map<String, String> data = new LinkedHashMap<>();
}

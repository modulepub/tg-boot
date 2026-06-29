package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "微信小程序多媒体内容安全异步检测请求")
public class WxMaMediaCheckAsyncRequest {

    @Schema(description = "小程序 appId；不传则使用首个启用配置")
    private String wxMaAppId;

    @Schema(description = "用户 openId")
    private String openId;

    @Schema(description = "场景：1资料 2评论 3论坛 4社交日志")
    private Integer scene;

    @Schema(description = "媒体 URL")
    private String mediaUrl;

    @Schema(description = "1音频 2图片")
    private Integer mediaType;
}

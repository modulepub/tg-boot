package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理端人工回复公众号粉丝消息。
 */
@Data
@Schema(description = "公众号消息人工回复 DTO")
public class WxMpMessageReplyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "公众号配置编码")
    private String wxMpConfigCode;

    @Schema(description = "粉丝 OpenId")
    private String wxMpFanOpenId;

    @Schema(description = "回复文本内容")
    private String content;
}

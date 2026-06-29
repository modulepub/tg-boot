package pub.module.wx.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import pub.module.common.model.po.BaseEntity;

import java.time.LocalDateTime;

/**
 * 微信公众号粉丝会话，表 wx_mp_fan。
 */
@Data
@TableName("wx_mp_fan")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title = "wx_mp_fan", description = "微信公众号粉丝会话")
public class WxMpFan extends BaseEntity {

    @Schema(description = "粉丝会话业务编码")
    private String wxMpFanCode;

    @Schema(description = "公众号配置编码")
    private String wxMpConfigCode;

    @Schema(description = "粉丝 OpenId")
    private String wxMpFanOpenId;

    @Schema(description = "昵称")
    private String wxMpFanNickname;

    @Schema(description = "关联 AI 对话会话编码")
    private String aiChatSessionCode;

    @Schema(description = "最近一条消息摘要")
    private String wxMpFanLastMessageContent;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最近消息时间")
    private LocalDateTime wxMpFanLastMessageTime;
}

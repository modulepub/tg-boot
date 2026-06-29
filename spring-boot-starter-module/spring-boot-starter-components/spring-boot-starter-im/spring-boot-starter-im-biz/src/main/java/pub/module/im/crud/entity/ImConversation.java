package pub.module.im.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * IM-会话
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_conversation")
@Schema(description = "IM-会话")
public class ImConversation extends BaseEntity {

    @Schema(description = "业务编码")
    private String imConversationCode;

    @Schema(description = "用户A编码")
    private String imConversationUserACode;

    @Schema(description = "用户B编码")
    private String imConversationUserBCode;

    @Schema(description = "最后一条消息编码")
    private String imConversationLastMessageCode;

    @Schema(description = "用户A未读数")
    private Integer imConversationUnreadCountA;

    @Schema(description = "用户B未读数")
    private Integer imConversationUnreadCountB;
}

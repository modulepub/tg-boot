package pub.module.im.crud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_user")
@Schema(description = "IM-用户")
public class ImUser extends BaseEntity {

    @Schema(description = "业务编码")
    private String imUserCode;

    @Schema(description = "IM用户编码")
    private String imUserUserCode;

    @Schema(description = "昵称")
    private String imUserNickName;

    @Schema(description = "头像")
    private String imUserAvatar;

    @Schema(description = "真实姓名")
    private String imUserRealName;

    @Schema(description = "标签")
    private String imUserTag;

    @Schema(description = "未读消息数（发给该用户且用户未读的消息数）")
    private Integer imUserUnreadCount;

    @TableField(exist = false)
    @Schema(description = "是否已与系统账号成为好友")
    private Boolean imUserSystemFriend;
}

package pub.module.im.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * IM-好友关系
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_friend")
@Schema(description = "IM-好友关系")
public class ImFriend extends BaseEntity {

    @Schema(description = "业务编码")
    private String imFriendCode;

    @Schema(description = "用户编码")
    private String imFriendUserCode;

    @Schema(description = "好友用户编码")
    private String imFriendFriendUserCode;

    @Schema(description = "好友备注")
    private String imFriendRemark;

    @Schema(description = "状态 0=删除 1=正常")
    private String imFriendStatusCode;
}

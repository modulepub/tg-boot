package pub.module.affines.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.common.model.po.BaseEntity;

/**
 * 家长关注
 */
@Data
@TableName("af_parent_follow")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "家长关注")
public class AfParentFollow extends BaseEntity {

    @Schema(description = "关注记录编码")
    private String afParentFollowCode;

    @Schema(description = "关注者用户编码")
    private String afFollowerUserCode;

    @Schema(description = "被关注资料卡编码")
    private String afTargetChildProfileCode;

    @Schema(description = "是否关注")
    private StatusCodeEnum afFollowStatusCode;
}

package pub.module.affines.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * 孩子资料卡浏览记录
 */
@Data
@TableName("af_child_profile_view")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "孩子资料卡浏览记录")
public class AfChildProfileView extends BaseEntity {

    @Schema(description = "浏览记录编码")
    private String afChildProfileViewCode;

    @Schema(description = "被浏览资料卡编码")
    private String afChildProfileCode;

    @Schema(description = "浏览者用户编码")
    private String afViewerUserCode;
}

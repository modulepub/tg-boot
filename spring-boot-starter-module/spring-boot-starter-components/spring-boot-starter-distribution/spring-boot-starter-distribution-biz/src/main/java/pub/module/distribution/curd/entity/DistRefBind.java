package pub.module.distribution.curd.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dist_ref_bind")
@Schema(description = "分销推荐关系绑定")
public class DistRefBind extends BaseEntity {

    private String distRefBindCode;
    private String distBizLineCode;
    private String distInviteeUserCode;
    private String distInviterUserCode;
    private String distRefBindSourceCode;
    private String distRefBindStatusCode;
    private LocalDateTime distBindTime;
}

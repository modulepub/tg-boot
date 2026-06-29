package pub.module.im.crud.entity;

import pub.module.common.enums.StatusCodeEnum;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_notice_recipient")
@Schema(description = "IM-通知接收记录")
public class ImNoticeRecipient extends BaseEntity {

    @Schema(description = "业务编码")
    private String imNcRpCode;

    @Schema(description = "通知编码")
    private String imNoticeCode;

    @Schema(description = "接收人IM用户编码")
    private String imNcRpUserCode;

    @Schema(description = "接收状态 1成功 0失败")
    private StatusCodeEnum imNcNpStatusCode;
}

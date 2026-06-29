package pub.module.im.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * IM-用户会话（多端登录记录）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_user_session")
@Schema(description = "IM-用户多端会话")
public class ImUserSession extends BaseEntity {

    @Schema(description = "业务编码")
    private String imUserSessionCode;

    @Schema(description = "用户编码")
    private String imUserSessionUserCode;

    @Schema(description = "客户端ID")
    private String imUserSessionClientId;

    @Schema(description = "设备类型")
    private String imUserSessionDeviceType;

    @Schema(description = "状态 0=离线 1=在线")
    private String imUserSessionStatusCode;
}
